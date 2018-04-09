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
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
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

    public static CopyOnWriteArrayList<DeployStatusEntity> deployStatusEntities = new CopyOnWriteArrayList<>();
    //    private String recvEndFlag = Utils.fixedLengthString("fileRecvEnd", 512);
    private String recvEndFlag = "fileRecvEnd";

    // 获取设备进程信息
    @Async
    public Future<DeviceTaskEntity> getDeviceTasks(String id, DeviceEntity deviceEntity) throws IOException, InterruptedException {
        Date startTime = new Date();
        udpService.sendGetDeviceTasksMessage(id, deviceEntity);
        logger.info("<" + deviceEntity.getIp() + ">--->获取进程信息报文发送成功，申请ID：" + id);
        int retryTimes = 0;
        while (true) {
            if (stringRedisTemplate.hasKey(id)) {
                logger.info("<" + deviceEntity.getIp() + ">--->" + "获取进程信息成功，总计耗时：" + (new Date().getTime() - startTime.getTime()) / 1000 + "秒");
                DeviceTaskEntity deviceTaskEntity = JsonUtils.readJsonString(stringRedisTemplate.opsForValue().get(id), DeviceTaskEntity.class);
                return new AsyncResult<>(deviceTaskEntity);
            } else {
                Thread.sleep(applicationConfiguration.getSocketTimeout() * 10);
                retryTimes = retryTimes + 1;
                if (retryTimes == applicationConfiguration.getMaxRetryTimes()) {
                    logger.info("<" + deviceEntity.getIp() + ">--->" + "获取进程信息等待超时。");
                    throw new CustomizeException("<" + deviceEntity.getIp() + ">--->" + "获取进程信息等待超时。");
                }
            }
        }
    }

    // 获取设备磁盘信息
    @Async
    public Future<DeviceDiskEntity> getDeviceDisks(String id, DeviceEntity deviceEntity) throws IOException, InterruptedException {
        Date startTime = new Date();
        udpService.sendGetDeviceDisksMessage(id, deviceEntity);
        logger.info("<" + deviceEntity.getIp() + ">--->获取磁盘信息报文发送成功，申请ID：" + id);
        int retryTimes = 0;
        while (true) {
            if (stringRedisTemplate.hasKey(id)) {
                logger.info("<" + deviceEntity.getIp() + ">--->" + "获取磁盘信息成功，总计耗时：" + (new Date().getTime() - startTime.getTime()) / 1000 + "秒");
                DeviceDiskEntity deviceDiskEntity = JsonUtils.readJsonString(stringRedisTemplate.opsForValue().get(id), DeviceDiskEntity.class);
                return new AsyncResult<>(deviceDiskEntity);
            } else {
                Thread.sleep(applicationConfiguration.getSocketTimeout() * 5);
                retryTimes = retryTimes + 1;
                if (retryTimes == applicationConfiguration.getMaxRetryTimes()) {
                    logger.info("<" + deviceEntity.getIp() + ">--->" + "获取磁盘信息等待超时。");
                    throw new CustomizeException("<" + deviceEntity.getIp() + ">--->" + "获取磁盘信息等待超时。");
                }
            }
        }
    }

    // 扫描设备
    @Async
    public Future<ScanResultEntity> scan(String id, DeploymentDesignDetailEntity deploymentDesignDetailEntity, String... extensions) throws IOException, InterruptedException {
        Date scanStartTime = new Date();
        String deployPath = (deploymentDesignDetailEntity.getDeviceEntity().getDeployPath() + deploymentDesignDetailEntity.getComponentEntity().getDeployPath()).replace("//", "/");
        if (extensions == null || extensions.length == 0) {
            udpService.sendScanDeviceOrderMessage(id, deploymentDesignDetailEntity.getDeviceEntity().getIp(), deploymentDesignDetailEntity.getDeviceEntity().getUDPPort(), deploymentDesignDetailEntity.getDeviceEntity().getId(), deploymentDesignDetailEntity.getComponentEntity().getId(), deployPath);
        } else {
            udpService.sendScanDeviceOrderMessage(id, deploymentDesignDetailEntity.getDeviceEntity().getIp(), deploymentDesignDetailEntity.getDeviceEntity().getUDPPort(), deploymentDesignDetailEntity.getDeviceEntity().getId(), deploymentDesignDetailEntity.getComponentEntity().getId(), deployPath, extensions);
        }
        logger.info("<" + deploymentDesignDetailEntity.getDeviceEntity().getIp() + ">:组件: " + deploymentDesignDetailEntity.getComponentEntity().getName() + "-" + deploymentDesignDetailEntity.getComponentEntity().getVersion() + "--->扫描报文发送成功，申请ID：" + id);
        int count = 0;
        while (true) {
            if (stringRedisTemplate.hasKey(id)) {
                logger.info("<" + deploymentDesignDetailEntity.getDeviceEntity().getIp() + ">:组件: " + deploymentDesignDetailEntity.getComponentEntity().getName() + "-" + deploymentDesignDetailEntity.getComponentEntity().getVersion() + "--->收到扫描结果,耗时：" + (new Date().getTime() - scanStartTime.getTime()) / 1000 + "秒");
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
                                logger.info("正确文件：" + componentFile.getPath() + "-标准MD5：" + componentFile.getMD5() + "-计算MD5：" + scanResult.getMD5());
                            } else {
                                modifyedComponentFiles.add(componentFile);
                                scanResultEntity.setHasModifyedComponentFiles(true);
                                logger.info("修改文件：" + componentFile.getPath() + "-标准MD5：" + componentFile.getMD5() + "-计算MD5：" + scanResult.getMD5());
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
                logger.info("<" + deploymentDesignDetailEntity.getDeviceEntity().getIp() + ">:组件: " + deploymentDesignDetailEntity.getComponentEntity().getName() + "-" + deploymentDesignDetailEntity.getComponentEntity().getVersion() + "--->扫描成功。");
                return new AsyncResult<>(scanResultEntity);
            } else {
                Thread.sleep(applicationConfiguration.getSocketTimeout() * 10);
                count = count + 1;
                if (count == applicationConfiguration.getMaxRetryTimes()) {
                    logger.info("<" + deploymentDesignDetailEntity.getDeviceEntity().getIp() + ">:组件: " + deploymentDesignDetailEntity.getComponentEntity().getName() + "-" + deploymentDesignDetailEntity.getComponentEntity().getVersion() + "--->扫描失败。");
                    throw new CustomizeException("<" + deploymentDesignDetailEntity.getDeviceEntity().getIp() + ">:组件: " + deploymentDesignDetailEntity.getComponentEntity().getName() + "-" + deploymentDesignDetailEntity.getComponentEntity().getVersion() + "--->扫描失败。");
                }
            }
        }
    }

    // 部署组件
    @Async
    public void deployDesign(DeviceEntity deviceEntity, List<DeploymentDesignDetailEntity> deploymentDesignDetailEntityList) {

        Socket socket;
        DataOutputStream dataOutputStream;
        DataInputStream dataInputStream;

        int totalFileNum = 0;
        int sendFileNum = 0;
        long sendFileSize = 0;
        Date deployStartTime = new Date();

        try {
            // 检查是否正在部署
            DeployStatusEntity deployStatusEntity = getDepoloyStatusEntity(deviceEntity.getIp());
            if (deployStatusEntity != null) {
                if (deployStatusEntity.isDeploying()) {
                    throw new CustomizeException(NotificationMessage.DEVICE_DEPLOYING);
                } else {
                    deployStatusEntities.remove(deployStatusEntity);
                    deployStatusEntities.add(new DeployStatusEntity(deviceEntity.getIp()));
                }
            } else {
                deployStatusEntities.add(new DeployStatusEntity(deviceEntity.getIp()));
            }

            // 计算总文件数量
            for (DeploymentDesignDetailEntity DeploymentDesignDetailEntity : deploymentDesignDetailEntityList) {
                totalFileNum = totalFileNum + DeploymentDesignDetailEntity.getComponentEntity().getComponentDetailEntities().size();
            }

            // 建立并配置TCP链接
            socket = new Socket(deviceEntity.getIp(), deviceEntity.getTCPPort());
            socket.setTcpNoDelay(true);
            socket.setSoTimeout(applicationConfiguration.getSocketTimeout());
            // 获取输入输出流
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());
            List<DeployLogDetailEntity> totalErrorFileList = new ArrayList<>();
            List<DeployLogDetailEntity> totalCompletedFileList = new ArrayList<>();
            // 循环发送组件
            for (DeploymentDesignDetailEntity deploymentDesignDetailEntity : deploymentDesignDetailEntityList) {
                ComponentEntity componentEntity = deploymentDesignDetailEntity.getComponentEntity();
                String deployPath = FormatUtils.pathFormat(deploymentDesignDetailEntity.getDeviceEntity().getDeployPath() + componentEntity.getDeployPath());
                if (componentEntity.getComponentDetailEntities().size() > 0) {
                    List<DeployLogDetailEntity> errorFileList = new ArrayList<>();
                    List<DeployLogDetailEntity> completedFileList = new ArrayList<>();
                    //建立部署日志
                    DeployLogEntity deployLogEntity = deployLogService.saveDeployLog(deviceEntity, componentEntity);
                    for (ComponentDetailEntity componentDetailEntity : componentEntity.getComponentDetailEntities()) {
                        // 发送文件开始标志
                        dataOutputStream.write("fileRecvStart".getBytes());
                        // 发送文件路径 + 文件名
                        String destPath = FormatUtils.pathFormat(deployPath + componentDetailEntity.getPath());
                        dataOutputStream.write(Utils.getString(destPath, 255 - (deployPath + componentDetailEntity.getPath()).getBytes().length).getBytes());
                        // 接收路径回复确认
                        int pathRetryCount = 0;
                        while (true) {
                            try {
                                if (dataInputStream.read() == 114) {
                                    break;
                                }
                            } catch (IOException exception) {
                                pathRetryCount = pathRetryCount + 1;
                                if (pathRetryCount == applicationConfiguration.getMaxWaitTimes()) {
                                    deployLogService.updateDeployLog(deployLogEntity, errorFileList, completedFileList, sendFileSize, DeployLogService.FAIL_STATE);
                                    getDepoloyStatusEntity(deviceEntity.getIp()).setDeploying(false);
                                    logger.info("<" + deviceEntity.getIp() + ">--->部署组件：" + componentEntity.getName() + "-" + componentEntity.getVersion() + "@" + destPath + "文件失败,发送终止。");
                                    throw new CustomizeException(NotificationMessage.PATH_ERROR);
                                }
                            }
                        }
                        // 发送文件实体
                        IOUtils.copy(new FileInputStream(componentEntity.getFilePath() + componentDetailEntity.getPath()), dataOutputStream);
                        // 单个文件发送结束标志d
                        dataOutputStream.write(recvEndFlag.getBytes());
                        // 接收结束标志回复
                        int endRetryCount = 0;
                        while (true) {
                            try {
                                if (dataInputStream.read() == 102) {
                                    // 文件发送成功
                                    sendFileNum = sendFileNum + 1;
                                    sendFileSize = sendFileSize + componentDetailEntity.getSize();
                                    // 更新设备发送进度
                                    DeployLogDetailEntity deployLogDetailEntity = new DeployLogDetailEntity(destPath, componentEntity, componentDetailEntity);
                                    completedFileList.add(deployLogDetailEntity);
                                    totalCompletedFileList.add(deployLogDetailEntity);
                                    updateDeployStatus(deviceEntity.getIp(), deployStartTime, totalErrorFileList, totalCompletedFileList, sendFileNum, totalFileNum, sendFileSize);
                                    logger.info("<" + deviceEntity.getIp() + ">--->部署组件：" + componentEntity.getName() + "-" + componentEntity.getVersion() + "@" + destPath + "成功，速度：" + getDepoloyStatusEntity(deviceEntity.getIp()).getTransferRate() + "kb/s");
                                    break;
                                }
                            } catch (IOException exception) {
                                endRetryCount = endRetryCount + 1;
                                dataOutputStream.write(recvEndFlag.getBytes());
                                if (endRetryCount == applicationConfiguration.getMaxWaitTimes()) {
                                    // 加入失败列表。
                                    DeployLogDetailEntity deployLogDetailEntity = new DeployLogDetailEntity(destPath, componentEntity, componentDetailEntity);
                                    errorFileList.add(deployLogDetailEntity);
                                    totalErrorFileList.add(deployLogDetailEntity);
                                    updateDeployStatus(deviceEntity.getIp(), deployStartTime, totalErrorFileList, totalCompletedFileList, sendFileNum, totalFileNum, sendFileSize);
                                    logger.info("<" + deviceEntity.getIp() + ">--->部署组件：" + componentEntity.getName() + "-" + componentEntity.getVersion() + "@" + destPath + "失败");
                                    break;
                                }
                            }
                        }
                    }
                    // 更新部署日志状态
                    if (errorFileList.size() != 0) {
                        deployLogService.updateDeployLog(deployLogEntity, errorFileList, completedFileList, sendFileSize, DeployLogService.FAIL_STATE);
                    } else {
                        deployLogService.updateDeployLog(deployLogEntity, errorFileList, completedFileList, sendFileSize, DeployLogService.COMPLETE_STATE);
                    }
                }
            }
            // 发送部署结束标志
            dataOutputStream.write("DeployEnd".getBytes());
            dataOutputStream.flush();
            dataOutputStream.close();
            socket.close();
            logger.info("<" + deviceEntity.getIp() + ">--->部署结束");
        } catch (IOException e) {
            e.printStackTrace();
            getDepoloyStatusEntity(deviceEntity.getIp()).setDeploying(false);
        } finally {
            getDepoloyStatusEntity(deviceEntity.getIp()).setDeploying(false);
        }
    }

    // 部署快照
    @Async
    public void deploySnapshot(String ip, int port, List<DeploymentDesignSnapshotDetailEntity> deploymentDesignSnapshotDetailEntityList) {

        Socket socket;
        DataOutputStream dataOutputStream;
        DataInputStream dataInputStream;

        int totalFileNum = 0;
        int sendFileNum = 0;
        long sendFileSize = 0;
        Date deployStartTime = new Date();

        try {

            // 检查是否正在部署
            DeployStatusEntity deployStatusEntity = getDepoloyStatusEntity(ip);
            if (deployStatusEntity != null) {
                if (deployStatusEntity.isDeploying()) {
                    throw new CustomizeException(NotificationMessage.DEVICE_DEPLOYING);
                } else {
                    deployStatusEntities.remove(deployStatusEntity);
                    deployStatusEntities.add(new DeployStatusEntity(ip));
                }
            } else {
                deployStatusEntities.add(new DeployStatusEntity(ip));
            }

            // 计算总文件数量
            for (DeploymentDesignSnapshotDetailEntity deploymentDesignSnapshotDetailEntity : deploymentDesignSnapshotDetailEntityList) {
                totalFileNum = totalFileNum + deploymentDesignSnapshotDetailEntity.getComponentEntity().getComponentDetailEntities().size();
            }

            // 建立并配置TCP链接
            socket = new Socket(ip, port);
            socket.setTcpNoDelay(true);
            socket.setSoTimeout(applicationConfiguration.getSocketTimeout());
            // 获取输入输出流
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());
            List<DeployLogDetailEntity> totalErrorFileList = new ArrayList<>();
            List<DeployLogDetailEntity> totalCompletedFileList = new ArrayList<>();
            // 循环发送组件
            logger.info("<" + ip + ">--->部署开始,文件发送结束标志符长度：" + recvEndFlag.getBytes().length);
            for (DeploymentDesignSnapshotDetailEntity deploymentDesignSnapshotDetailEntity : deploymentDesignSnapshotDetailEntityList) {
                List<DeployLogDetailEntity> errorFileList = new ArrayList<>();
                List<DeployLogDetailEntity> completedFileList = new ArrayList<>();
                ComponentEntity componentEntity = deploymentDesignSnapshotDetailEntity.getComponentEntity();
                String deployPath = FormatUtils.pathFormat(deploymentDesignSnapshotDetailEntity.getDeployPath() + componentEntity.getDeployPath());
                if (componentEntity.getComponentDetailEntities().size() > 0) {
                    //建立部署日志
                    DeployLogEntity deployLogEntity = deployLogService.saveDeployLog(deploymentDesignSnapshotDetailEntity, componentEntity);
                    for (ComponentDetailEntity componentDetailEntity : componentEntity.getComponentDetailEntities()) {
                        // 发送文件开始标志
                        dataOutputStream.write("fileRecvStart".getBytes());
                        // 发送文件路径 + 文件名
                        String destPath = FormatUtils.pathFormat(deployPath + componentDetailEntity.getPath());
                        dataOutputStream.write(Utils.getString(destPath, 255 - (deployPath + componentDetailEntity.getPath()).getBytes().length).getBytes());
                        // 接收路径回复确认
                        int pathRetryCount = 0;
                        while (true) {
                            try {
                                if (dataInputStream.read() == 114) {
                                    break;
                                }
                            } catch (IOException exception) {
                                pathRetryCount = pathRetryCount + 1;
                                if (pathRetryCount == applicationConfiguration.getMaxWaitTimes()) {
                                    deployLogService.updateDeployLog(deployLogEntity, errorFileList, completedFileList, sendFileSize, DeployLogService.FAIL_STATE);
                                    getDepoloyStatusEntity(ip).setDeploying(false);
                                    logger.info("<" + ip + ">--->部署" + destPath + "文件失败,发送终止。");
                                    throw new CustomizeException(NotificationMessage.PATH_ERROR);
                                }
                            }
                        }
                        // 发送文件实体
                        IOUtils.copy(new FileInputStream(componentEntity.getFilePath() + componentDetailEntity.getPath()), dataOutputStream);
                        // 单个文件发送结束标志
                        dataOutputStream.write(recvEndFlag.getBytes());
                        // 接收结束标志回复
                        int endRetryCount = 0;
                        while (true) {
                            try {
                                if (dataInputStream.read() == 102) {
                                    // 文件发送成功
                                    sendFileNum = sendFileNum + 1;
                                    sendFileSize = sendFileSize + componentDetailEntity.getSize();
                                    // 更新设备发送进度
                                    DeployLogDetailEntity deployLogDetailEntity = new DeployLogDetailEntity(destPath, componentEntity, componentDetailEntity);
                                    completedFileList.add(deployLogDetailEntity);
                                    totalCompletedFileList.add(deployLogDetailEntity);
                                    updateDeployStatus(ip, deployStartTime, totalErrorFileList, totalCompletedFileList, sendFileNum, totalFileNum, sendFileSize);
                                    logger.info("<" + ip + ">--->上部署" + destPath + "成功，速度：" + getDepoloyStatusEntity(ip).getTransferRate() + "kb/s");
                                    break;
                                }
                            } catch (IOException exception) {
                                endRetryCount = endRetryCount + 1;
                                dataOutputStream.write(recvEndFlag.getBytes());
                                if (endRetryCount == applicationConfiguration.getMaxWaitTimes()) {
                                    // 加入失败列表。
                                    DeployLogDetailEntity deployLogDetailEntity = new DeployLogDetailEntity(destPath, componentEntity, componentDetailEntity);
                                    errorFileList.add(deployLogDetailEntity);
                                    totalErrorFileList.add(deployLogDetailEntity);
                                    updateDeployStatus(ip, deployStartTime, totalErrorFileList, totalCompletedFileList, sendFileNum, totalFileNum, sendFileSize);
                                    logger.info("<" + ip + ">--->部署" + destPath + "失败");
                                    break;
                                }
                            }
                        }
                    }
                    // 更新部署日志状态
                    if (errorFileList.size() != 0) {
                        deployLogService.updateDeployLog(deployLogEntity, errorFileList, completedFileList, sendFileSize, DeployLogService.FAIL_STATE);
                    } else {
                        deployLogService.updateDeployLog(deployLogEntity, errorFileList, completedFileList, sendFileSize, DeployLogService.COMPLETE_STATE);
                    }
                }
            }
            // 发送部署结束标志
            dataOutputStream.write("DeployEnd".getBytes());
            dataOutputStream.flush();
            dataOutputStream.close();
            socket.close();
            logger.info("<" + ip + ">--->部署结束");
        } catch (IOException e) {
            e.printStackTrace();
            getDepoloyStatusEntity(ip).setDeploying(false);
        } finally {
            getDepoloyStatusEntity(ip).setDeploying(false);
        }
    }

    private void updateDeployStatus(String ip, Date deployStartTime, List<DeployLogDetailEntity> errorFileList, List<DeployLogDetailEntity> completedFileList, int sendFileNum, int totalFileNum, long sendFileSize) {
        DeployStatusEntity deployStatusEntity = getDepoloyStatusEntity(ip);
        deployStatusEntity.setErrorFileList(errorFileList);
        deployStatusEntity.setCompletedFileList(completedFileList);
        // 计算发送进度
        deployStatusEntity.setProgress(FormatUtils.doubleFormater(((double) sendFileNum / totalFileNum) * 100, FormatUtils.doubleFormatPattern));
        // 计算发送速度
        long time = (new Date().getTime() - deployStartTime.getTime()) / 1000;
        double transferRate = time == 0 ? 0 : ((double) sendFileSize / 1024) / time;
        deployStatusEntity.setTransferRate(FormatUtils.doubleFormater(transferRate, FormatUtils.doubleFormatPattern));
        deployStatusEntity.setDeploying(true);
    }

    private DeployStatusEntity getDepoloyStatusEntity(String ip) {
        for (DeployStatusEntity deployStatusEntity : deployStatusEntities) {
            if (deployStatusEntity.getIp().equals(ip)) {
                return deployStatusEntity;
            }
        }
        return null;
    }
}
