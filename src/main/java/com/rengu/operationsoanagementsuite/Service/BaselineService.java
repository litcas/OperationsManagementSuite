package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.*;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.BaselineRepository;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import com.rengu.operationsoanagementsuite.Utils.Tools;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Service
public class BaselineService {

    @Autowired
    private BaselineRepository baselineRepository;
    @Autowired
    private DeployPlanService deployPlanService;
    @Autowired
    private ProjectService projectService;

    @Transactional
    public BaselineEntity saveBaselines(String projectId, String deployplanId, BaselineEntity baselineArgs) {
        if (projectService.hasProject(projectId)) {
            throw new CustomizeException(NotificationMessage.PROJECT_NOT_FOUND);
        }
        if (deployPlanService.hasDeployPlans(deployplanId)) {
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_NOT_FOUND);
        }
        BaselineEntity baselineEntity = new BaselineEntity();
        BeanUtils.copyProperties(baselineArgs, baselineEntity, "id", "createTime", "baselineDetailEntities", "projectEntity");
        DeployPlanEntity deployPlanEntity = deployPlanService.getDeployPlans(deployplanId);
        baselineEntity.setProjectEntity(projectService.getProject(projectId));
        List<BaselineDetailEntity> baselineDetailEntityList = new ArrayList<>();
        for (DeployPlanDetailEntity deployPlanDetailEntity : deployPlanEntity.getDeployPlanDetailEntities()) {
            BaselineDetailEntity baselineDetailEntity = new BaselineDetailEntity();
            baselineDetailEntity.setDeviceName(deployPlanDetailEntity.getDeviceEntity().getName());
            baselineDetailEntity.setDeviceIp(deployPlanDetailEntity.getDeviceEntity().getIp());
            baselineDetailEntity.setTCPPort(deployPlanDetailEntity.getDeviceEntity().getTCPPort());
            baselineDetailEntity.setComponentEntity(deployPlanDetailEntity.getComponentEntity());
            baselineDetailEntity.setDeployPath(deployPlanDetailEntity.getDeployPath());
            baselineDetailEntityList.add(baselineDetailEntity);
        }
        baselineEntity.setBaselineDetailEntities(baselineDetailEntityList);
        return baselineRepository.save(baselineEntity);
    }

    @Transactional
    public void deleteBaselines(String baselinesId) {
        baselineRepository.delete(baselinesId);
    }

    @Transactional
    public List<BaselineEntity> getBaselinesByProjectId(String projectId) {
        if (projectService.hasProject(projectId)) {
            throw new CustomizeException(NotificationMessage.PROJECT_NOT_FOUND);
        }
        return baselineRepository.findByProjectEntityId(projectId);
    }

    @Transactional
    public BaselineEntity getBaselines(String baselinesId) {
        return baselineRepository.findOne(baselinesId);
    }

    @Transactional
    public void deployBaselines(String baselineId) throws IOException, InterruptedException {
        BaselineEntity baselineEntity = baselineRepository.findOne(baselineId);
        for (BaselineDetailEntity baselineDetailEntity : baselineEntity.getBaselineDetailEntities()) {
            startDeploy(baselineDetailEntity);
        }
    }

    // 异步发送文件
    @Async
    void startDeploy(BaselineDetailEntity baselineDetailEntity) throws IOException, InterruptedException {
        Socket socket = new Socket(baselineDetailEntity.getDeviceIp(), baselineDetailEntity.getTCPPort());
        if (socket.isConnected()) {
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            // 连接成功
            ComponentEntity componentEntity = baselineDetailEntity.getComponentEntity();
            for (ComponentFileEntity componentFileEntity : componentEntity.getComponentFileEntities()) {
                // 发送文件逻辑
                dataOutputStream.write("fileRecvStart".getBytes());
                // 1、发送文件路径 + 文件名
                String deployPath = Tools.getString(baselineDetailEntity.getDeployPath() + componentFileEntity.getPath(), 255 - (baselineDetailEntity.getDeployPath() + componentFileEntity.getPath()).getBytes().length);
                dataOutputStream.write(deployPath.getBytes());
                // 3、发送文件实体
                IOUtils.copy(new FileInputStream(componentEntity.getFilePath() + componentFileEntity.getPath()), dataOutputStream);
                // 4、单个文件发送结束标志
                dataOutputStream.write("fileRecvEnd".getBytes());
                Thread.sleep(1000);
            }
            // 5、发送部署结束标志
            dataOutputStream.write("DeployEnd".getBytes());
            dataOutputStream.flush();
            dataOutputStream.close();
            socket.close();
        }
    }
}
