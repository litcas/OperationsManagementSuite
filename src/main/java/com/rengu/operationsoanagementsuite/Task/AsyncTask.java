package com.rengu.operationsoanagementsuite.Task;

import com.rengu.operationsoanagementsuite.Entity.*;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Service.DeployLogService;
import com.rengu.operationsoanagementsuite.Service.DeploymentDesignService;
import com.rengu.operationsoanagementsuite.Service.DeviceService;
import com.rengu.operationsoanagementsuite.Service.UDPService;
import com.rengu.operationsoanagementsuite.Utils.JsonUtils;
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
import java.util.List;
import java.util.concurrent.Future;

@Service
public class AsyncTask {

    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private DeployLogService deployLogService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UDPService udpService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DeploymentDesignService deploymentDesignService;

    // 部署组件异步方法
    @Async
    public void deployDesign(String deploymentDesignId, String deviceId, List<DeploymentDesignDetailEntity> deploymentDesignDetailEntityList) throws IOException {
        DeploymentDesignEntity deploymentDesignEntity = deploymentDesignService.getDeploymentDesigns(deploymentDesignId);
        DeviceEntity deviceEntity = deviceService.getDevices(deviceId);
        Socket socket = new Socket(deviceEntity.getIp(), deviceEntity.getTCPPort());
        socket.setSoTimeout(2000);
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        for (DeploymentDesignDetailEntity deploymentDesignDetailEntity : deploymentDesignDetailEntityList) {
            ComponentEntity componentEntity = deploymentDesignDetailEntity.getComponentEntity();
            deploy(dataOutputStream, dataInputStream, componentEntity, deploymentDesignDetailEntity.getDeployPath());
        }
        // 发送部署结束标志
        dataOutputStream.write("DeployEnd".getBytes());
        dataOutputStream.flush();
        dataOutputStream.close();
        socket.close();
        deployLogService.saveDeployLogs(deploymentDesignEntity, deviceEntity, deploymentDesignDetailEntityList);
    }

    // 部署组件异步方法
    @Async
    public void deploySnapshot(String ip, int port, List<DeploymentDesignSnapshotDetailEntity> deploymentDesignSnapshotDetailEntityList) throws IOException {
        Socket socket = new Socket(ip, port);
        socket.setSoTimeout(2000);
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        for (DeploymentDesignSnapshotDetailEntity deploymentDesignSnapshotDetailEntity : deploymentDesignSnapshotDetailEntityList) {
            ComponentEntity componentEntity = deploymentDesignSnapshotDetailEntity.getComponentEntity();
            deploy(dataOutputStream, dataInputStream, componentEntity, deploymentDesignSnapshotDetailEntity.getDeployPath());
        }
        // 发送部署结束标志
        dataOutputStream.write("DeployEnd".getBytes());
        dataOutputStream.flush();
        dataOutputStream.close();
        socket.close();
    }

    // 部署
    private void deploy(DataOutputStream dataOutputStream, DataInputStream dataInputStream, ComponentEntity componentEntity, String deployPath) throws IOException {
        for (ComponentDetailEntity componentDetailEntity : componentEntity.getComponentDetailEntities()) {
            // 组件部署逻辑
            dataOutputStream.write("fileRecvStart".getBytes());
            // 发送文件路径 + 文件名
            String destPath = Utils.getString(deployPath + componentDetailEntity.getPath(), 255 - (deployPath + componentDetailEntity.getPath()).getBytes().length);
            dataOutputStream.write(destPath.getBytes());
            // 发送文件实体
            IOUtils.copy(new FileInputStream(componentEntity.getFilePath() + componentDetailEntity.getPath()), dataOutputStream);
            // 单个文件发送结束标志
            dataOutputStream.write("fileRecvEnd".getBytes());
            // 重复发送文件结束标志并等待回复
            int count = 0;
            while (true) {
                try {
                    if (dataInputStream.read() == 102) {
                        logger.info("文件名：" + componentDetailEntity.getPath() + "大小：" + componentDetailEntity.getSize() + "发送成功。");
                        break;
                    }
                } catch (IOException exception) {
                    count = count + 1;
                    dataOutputStream.write("fileRecvEnd".getBytes());
                    logger.info("文件发送结束标志回复等待超时，第" + count + "次重新发送文件结束标志。");
                    if (count == 10) {
                        logger.info("文件发送结束标志等待回复超时，" + deployPath + componentDetailEntity.getPath() + "发送失败");
                        break;
                    }
                }
            }
        }
    }

    // 扫描设备
    @Async
    public Future<ScanResultEntity> scan(String id, DeploymentDesignDetailEntity deploymentDesignDetailEntity, String... extensions) throws IOException, InterruptedException {
        if (extensions == null) {
            udpService.sendScanDeviceOrderMessage(id, deploymentDesignDetailEntity.getDeviceEntity().getIp(), deploymentDesignDetailEntity.getDeviceEntity().getUDPPort(), deploymentDesignDetailEntity.getDeviceEntity().getId(), deploymentDesignDetailEntity.getComponentEntity().getId(), deploymentDesignDetailEntity.getDeployPath());
        } else {
            udpService.sendScanDeviceOrderMessage(id, deploymentDesignDetailEntity.getDeviceEntity().getIp(), deploymentDesignDetailEntity.getDeviceEntity().getUDPPort(), deploymentDesignDetailEntity.getDeviceEntity().getId(), deploymentDesignDetailEntity.getComponentEntity().getId(), deploymentDesignDetailEntity.getDeployPath(), extensions);
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
                        if (scanResult.getPath().replace(deploymentDesignDetailEntity.getDeployPath(), "").equals(componentFile.getPath())) {
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
                Thread.sleep(10000);
                count = count + 1;
                if (count == 10) {
                    throw new CustomizeException("扫描'" + deploymentDesignDetailEntity.getDeviceEntity().getIp() + "'上的'" + deploymentDesignDetailEntity.getComponentEntity().getName() + "-" + deploymentDesignDetailEntity.getComponentEntity().getVersion() + "'组件失败");
                }
            }
        }
    }
}
