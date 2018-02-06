package com.rengu.operationsoanagementsuite.Task;

import com.rengu.operationsoanagementsuite.Entity.ComponentDetailEntity;
import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Entity.DeploymentDesignDetailEntity;
import com.rengu.operationsoanagementsuite.Entity.DeviceEntity;
import com.rengu.operationsoanagementsuite.Utils.Utils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

@Service
public class AsyncTask {

    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 部署组件异步方法
    @Async
    public void deploy(DeviceEntity deviceEntity, List<DeploymentDesignDetailEntity> deploymentDesignDetailEntityList) throws IOException {
        Socket socket = new Socket(deviceEntity.getIp(), deviceEntity.getTCPPort());
        socket.setSoTimeout(2000);
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        for (DeploymentDesignDetailEntity deploymentDesignDetailEntity : deploymentDesignDetailEntityList) {
            ComponentEntity componentEntity = deploymentDesignDetailEntity.getComponentEntity();
            for (ComponentDetailEntity componentDetailEntity : componentEntity.getComponentDetailEntities()) {
                // 组件部署逻辑
                dataOutputStream.write("fileRecvStart".getBytes());
                // 发送文件路径 + 文件名
                String destPath = Utils.getString(deploymentDesignDetailEntity.getDeployPath() + componentDetailEntity.getPath(), 255 - (deploymentDesignDetailEntity.getDeployPath() + componentDetailEntity.getPath()).getBytes().length);
                dataOutputStream.write(destPath.getBytes());
                // 发送文件实体
                IOUtils.copy(new FileInputStream(componentEntity.getFilePath() + componentDetailEntity.getPath()), dataOutputStream);
                // 单个文件发送结束标志
                dataOutputStream.write("fileRecvEnd".getBytes());
                // 重复发送文件结束标志并等待回复
                while (true) {
                    try {
                        if (dataInputStream.read() == 102) {
                            logger.info("文件名：" + componentDetailEntity.getPath() + "大小：" + componentDetailEntity.getSize() + "发送成功。");
                            break;
                        }
                    } catch (IOException exception) {
                        dataOutputStream.write("fileRecvEnd".getBytes());
                        logger.info("文件发送结束标志等待超时，重新发送文件结束标志。");
                    }
                }
            }
        }
        // 发送部署结束标志
        dataOutputStream.write("DeployEnd".getBytes());
        dataOutputStream.flush();
        dataOutputStream.close();
        socket.close();
    }
}
