package com.rengu.operationsoanagementsuite.Task;

import com.rengu.operationsoanagementsuite.Configuration.ApplicationConfiguration;
import com.rengu.operationsoanagementsuite.Entity.*;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Service.DeployLogService;
import com.rengu.operationsoanagementsuite.Service.UDPService;
import com.rengu.operationsoanagementsuite.Utils.FormatUtils;
import com.rengu.operationsoanagementsuite.Utils.JsonUtils;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import com.rengu.operationsoanagementsuite.Utils.Utils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

@Service
public class AsyncTask {

    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ApplicationConfiguration applicationConfiguration;
    @Autowired
    private UDPService udpService;
    @Autowired
    private DeployLogService deployLogService;

    // 部署组件异步方法
    @Async
    public Future<List<DeployResultEntity>> deployDesign(DeviceEntity deviceEntity, List<DeploymentDesignDetailEntity> deploymentDesignDetailEntityList) throws IOException {

        int size = 0;
        int sendCount = 0;
        List<DeployResultEntity> deployResultEntityList = new ArrayList<>();

        Socket socket = new Socket(deviceEntity.getIp(), deviceEntity.getTCPPort());
        socket.setTcpNoDelay(true);
        socket.setSoTimeout(applicationConfiguration.getSocketTimeout());
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        stringRedisTemplate.opsForValue().getAndSet(deviceEntity.getId(), String.valueOf(FormatUtils.doubleFormater(0, FormatUtils.doubleFormatPattern)));
        for (DeploymentDesignDetailEntity DeploymentDesignDetailEntity : deploymentDesignDetailEntityList) {
            size = size + DeploymentDesignDetailEntity.getComponentEntity().getComponentDetailEntities().size();
        }
        for (DeploymentDesignDetailEntity deploymentDesignDetailEntity : deploymentDesignDetailEntityList) {
            ComponentEntity componentEntity = deploymentDesignDetailEntity.getComponentEntity();
            String deployPath = (deploymentDesignDetailEntity.getDeviceEntity().getDeployPath() + componentEntity.getDeployPath()).replace("//", "/");
            TupleEntity tupleEntity = deploy(deviceEntity.getId(), size, sendCount, deployLogService.saveDeployLog(deviceEntity, componentEntity), dataOutputStream, dataInputStream, componentEntity, deployPath);
            sendCount = tupleEntity.getSendCount();
            deployResultEntityList.add(new DeployResultEntity(componentEntity, tupleEntity.getErrorFileList(), tupleEntity.getCompletedFileList()));
        }
        // 发送部署结束标志
        dataOutputStream.write("DeployEnd".getBytes());
        dataOutputStream.flush();
        dataOutputStream.close();
        socket.close();
        return new AsyncResult<>(deployResultEntityList);
    }

    // 部署组件异步方法
    @Async
    public Future<List<DeployResultEntity>> deploySnapshot(String deploymentdesignsnapshotId, String ip, int port, List<DeploymentDesignSnapshotDetailEntity> deploymentDesignSnapshotDetailEntityList) throws IOException {

        int size = 0;
        int sendCount = 0;
        List<DeployResultEntity> deployResultEntityList = new ArrayList<>();

        Socket socket = new Socket(ip, port);
        socket.setTcpNoDelay(true);
        socket.setSoTimeout(applicationConfiguration.getSocketTimeout());
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        stringRedisTemplate.opsForValue().getAndSet(deploymentdesignsnapshotId, String.valueOf(FormatUtils.doubleFormater(0, FormatUtils.doubleFormatPattern)));
        for (DeploymentDesignSnapshotDetailEntity deploymentDesignSnapshotDetailEntity : deploymentDesignSnapshotDetailEntityList) {
            size = size + deploymentDesignSnapshotDetailEntity.getComponentEntity().getComponentDetailEntities().size();
        }
        for (DeploymentDesignSnapshotDetailEntity deploymentDesignSnapshotDetailEntity : deploymentDesignSnapshotDetailEntityList) {
            ComponentEntity componentEntity = deploymentDesignSnapshotDetailEntity.getComponentEntity();
            TupleEntity tupleEntity = deploy(deploymentdesignsnapshotId, size, sendCount, deployLogService.saveDeployLog(deploymentDesignSnapshotDetailEntity, componentEntity), dataOutputStream, dataInputStream, componentEntity, deploymentDesignSnapshotDetailEntity.getDeployPath());
            sendCount = tupleEntity.getSendCount();
            deployResultEntityList.add(new DeployResultEntity(componentEntity, tupleEntity.getErrorFileList(), tupleEntity.getCompletedFileList()));
        }
        // 发送部署结束标志
        dataOutputStream.write("DeployEnd".getBytes());
        dataOutputStream.flush();
        dataOutputStream.close();
        socket.close();
        return new AsyncResult<>(deployResultEntityList);
    }

    // 部署
    private TupleEntity deploy(String id, int size, int sendCount, DeployLogEntity deployLogEntity, DataOutputStream dataOutputStream, DataInputStream dataInputStream, ComponentEntity componentEntity, String deployPath) throws IOException {
        int fileNum = 0;
        long sendSize = 0;
        List<DeployFileEntity> errorFileList = new ArrayList<>();
        List<DeployFileEntity> completedFileList = new ArrayList<>();
        for (ComponentDetailEntity componentDetailEntity : componentEntity.getComponentDetailEntities()) {
            fileNum = fileNum + 1;
            // 组件部署逻辑
            dataOutputStream.write("fileRecvStart".getBytes());
            // 发送文件路径 + 文件名
            String destPath = Utils.getString((deployPath + componentDetailEntity.getPath()).replace("//", "/"), 255 - (deployPath + componentDetailEntity.getPath()).getBytes().length);
            dataOutputStream.write(destPath.getBytes());
            int pathRetryCount = 0;
            while (true) {
                try {
                    if (dataInputStream.read() == 114) {
                        break;
                    }
                } catch (IOException exception) {
                    pathRetryCount = pathRetryCount + 1;
                    if (pathRetryCount == applicationConfiguration.getMaxWaitTimes()) {
                        deployLogService.updateDeployLog(deployLogEntity, sendSize, errorFileList.size(), completedFileList.size(), DeployLogService.FAIL_STATE);
                        throw new CustomizeException(NotificationMessage.DISK_NOT_FOUND);
                    }
                }
            }
            // 发送文件实体
            IOUtils.copy(new FileInputStream(componentEntity.getFilePath() + componentDetailEntity.getPath()), dataOutputStream);
            // 单个文件发送结束标志
            dataOutputStream.write("fileRecvEnd".getBytes());
            // 重复发送文件结束标志并等待回复
            int count = 0;
            while (true) {
                try {
                    if (dataInputStream.read() == 102) {
                        sendCount = sendCount + 1;
                        sendSize = sendSize + componentDetailEntity.getSize();
                        completedFileList.add(new DeployFileEntity(componentEntity, componentDetailEntity, deployPath + componentDetailEntity.getPath()));
                        stringRedisTemplate.opsForValue().getAndSet(id, String.valueOf(FormatUtils.doubleFormater((sendCount / (double) size) * 100, FormatUtils.doubleFormatPattern)));
                        logger.info("部署路径：" + (deployPath + componentDetailEntity.getPath()).replace("//", "/") + ",MD5:" + componentDetailEntity.getMD5() + ",大小：" + componentDetailEntity.getDisplaySize() + ",发送成功,当前发送进度：" + FormatUtils.doubleFormater((sendCount / (double) size) * 100, FormatUtils.doubleFormatPattern) + "%(" + fileNum + "/" + componentEntity.getComponentDetailEntities().size() + ")");
                        break;
                    }
                } catch (IOException exception) {
                    count = count + 1;
                    dataOutputStream.write("fileRecvEnd".getBytes());
                    if (count == applicationConfiguration.getMaxWaitTimes()) {
                        errorFileList.add(new DeployFileEntity(componentEntity, componentDetailEntity, deployPath + componentDetailEntity.getPath()));
                        logger.info("部署路径：" + (deployPath + componentDetailEntity.getPath()).replace("//", "/") + ",MD5:" + componentDetailEntity.getMD5() + ",大小：" + componentDetailEntity.getDisplaySize() + ",发送失败(" + fileNum + "/" + componentEntity.getComponentDetailEntities().size() + ")");
                        break;
                    }
                }
            }
        }
        // 重新发送失败文件
        int reSendCount = 0;
        while (errorFileList.size() != 0) {
            reSendCount = reSendCount + 1;
            if (reSendCount == applicationConfiguration.getMaxRetryTimes()) {
                break;
            }
            Iterator<DeployFileEntity> deployFileEntityIterable = errorFileList.iterator();
            while (deployFileEntityIterable.hasNext()) {
                DeployFileEntity deployFileEntity = deployFileEntityIterable.next();
                // 组件部署逻辑
                dataOutputStream.write("fileRecvStart".getBytes());
                // 发送文件路径 + 文件名
                String destPath = Utils.getString(deployFileEntity.getDestPath(), 255 - (deployFileEntity.getDestPath()).getBytes().length);
                dataOutputStream.write(destPath.getBytes());
                int pathRetryCount = 0;
                while (true) {
                    try {
                        if (dataInputStream.read() == 114) {
                            break;
                        }
                    } catch (IOException exception) {
                        pathRetryCount = pathRetryCount + 1;
                        if (pathRetryCount == applicationConfiguration.getMaxRetryTimes() / 2) {
                            deployLogService.updateDeployLog(deployLogEntity, sendSize, errorFileList.size(), completedFileList.size(), DeployLogService.FAIL_STATE);
                            throw new CustomizeException(NotificationMessage.DISK_NOT_FOUND);
                        }
                    }
                }
                // 发送文件实体
                IOUtils.copy(new FileInputStream(deployFileEntity.getComponentEntity().getFilePath() + deployFileEntity.getComponentDetailEntity().getPath()), dataOutputStream);
                // 单个文件发送结束标志
                dataOutputStream.write("fileRecvEnd".getBytes());
                // 重复发送文件结束标志并等待回复
                int count = 0;
                while (true) {
                    try {
                        if (dataInputStream.read() == 102) {
                            deployFileEntityIterable.remove();
                            sendCount = sendCount + 1;
                            sendSize = sendSize + deployFileEntity.getComponentDetailEntity().getSize();
                            completedFileList.add(new DeployFileEntity(componentEntity, deployFileEntity.getComponentDetailEntity(), deployFileEntity.getDestPath()));
                            stringRedisTemplate.opsForValue().getAndSet(id, String.valueOf(FormatUtils.doubleFormater((sendCount / (double) size) * 100, FormatUtils.doubleFormatPattern)));
                            logger.info("部署路径：" + deployFileEntity.getDestPath() + ",MD5:" + deployFileEntity.getComponentDetailEntity().getMD5() + ",大小：" + deployFileEntity.getComponentDetailEntity().getDisplaySize() + ",重新发送成功,当前发送进度:" + FormatUtils.doubleFormater((sendCount / (double) size) * 100, FormatUtils.doubleFormatPattern) + "%(剩余发送失败文件数量：" + errorFileList.size() + ")");
                            break;
                        }
                    } catch (IOException exception) {
                        count = count + 1;
                        dataOutputStream.write("fileRecvEnd".getBytes());
                        if (count == applicationConfiguration.getMaxWaitTimes() / 2) {
                            break;
                        }
                    }
                }
            }
        }
        if (errorFileList.size() != 0) {
            deployLogService.updateDeployLog(deployLogEntity, sendSize, errorFileList.size(), completedFileList.size(), DeployLogService.FAIL_STATE);
        } else {
            deployLogService.updateDeployLog(deployLogEntity, sendSize, errorFileList.size(), completedFileList.size(), DeployLogService.COMPLETE_STATE);
        }
        return new TupleEntity(sendCount, errorFileList, completedFileList);
    }

    // 扫描设备
    @Async
    public Future<ScanResultEntity> scan(String id, DeploymentDesignDetailEntity deploymentDesignDetailEntity, String... extensions) throws IOException, InterruptedException {
        String deployPath = (deploymentDesignDetailEntity.getDeviceEntity().getDeployPath() + deploymentDesignDetailEntity.getComponentEntity().getDeployPath()).replace("//", "/");
        if (extensions == null || extensions.length == 0) {
            udpService.sendScanDeviceOrderMessage(id, deploymentDesignDetailEntity.getDeviceEntity().getIp(), deploymentDesignDetailEntity.getDeviceEntity().getUDPPort(), deploymentDesignDetailEntity.getDeviceEntity().getId(), deploymentDesignDetailEntity.getComponentEntity().getId(), deployPath);
        } else {
            udpService.sendScanDeviceOrderMessage(id, deploymentDesignDetailEntity.getDeviceEntity().getIp(), deploymentDesignDetailEntity.getDeviceEntity().getUDPPort(), deploymentDesignDetailEntity.getDeviceEntity().getId(), deploymentDesignDetailEntity.getComponentEntity().getId(), deployPath, extensions);
        }
        int count = 0;
        while (true) {
            if (stringRedisTemplate.hasKey(id)) {
                ScanResultEntity scanResultEntity = JsonUtils.readJsonString(stringRedisTemplate.opsForValue().get(id), ScanResultEntity.class);
                ComponentEntity componentEntity = deploymentDesignDetailEntity.getComponentEntity();
                List<ComponentDetailEntity> correctComponentFiles = new ArrayList<>();
                List<ComponentDetailEntity> modifyedComponentFiles = new ArrayList<>();
                List<ComponentDetailEntity> unknownFiles = new ArrayList<>();
                for (ComponentDetailEntity scanResult : scanResultEntity.getOriginalScanResultList()) {
                    boolean fileExists = false;
                    for (ComponentDetailEntity componentFile : componentEntity.getComponentDetailEntities()) {
                        // 路径是否一致
                        if (scanResult.getPath().replace(deployPath, "/").equals(componentFile.getPath())) {
                            fileExists = true;
                            // MD5是否相同
                            if (scanResult.getMD5().equals(componentFile.getMD5())) {
                                correctComponentFiles.add(componentFile);
                                scanResultEntity.setHasCorrectComponentFiles(true);
                            } else {
                                modifyedComponentFiles.add(componentFile);
                                scanResultEntity.setHasModifyedComponentFiles(true);
                            }
                            break;
                        }
                    }
                    // 未知文件
                    if (!fileExists) {
                        scanResultEntity.setHasUnknownFiles(true);
                        ComponentDetailEntity componentFile = new ComponentDetailEntity();
                        componentFile.setMD5(scanResult.getMD5());
                        componentFile.setPath(scanResult.getPath());
                        unknownFiles.add(componentFile);
                    }
                }
                scanResultEntity.setCorrectComponentFiles(correctComponentFiles);
                scanResultEntity.setModifyedComponentFiles(modifyedComponentFiles);
                scanResultEntity.setUnknownFiles(unknownFiles);
                scanResultEntity.setHasMissingFile(scanResultEntity.getOriginalScanResultList().size() - unknownFiles.size() != componentEntity.getComponentDetailEntities().size());
                return new AsyncResult<>(scanResultEntity);
            } else {
                Thread.sleep(applicationConfiguration.getSocketTimeout() * 10);
                count = count + 1;
                if (count == applicationConfiguration.getMaxRetryTimes()) {
                    throw new CustomizeException("扫描'" + deploymentDesignDetailEntity.getDeviceEntity().getIp() + "'上的'" + deploymentDesignDetailEntity.getComponentEntity().getName() + "-" + deploymentDesignDetailEntity.getComponentEntity().getVersion() + "'组件超时。");
                }
            }
        }
    }
}
