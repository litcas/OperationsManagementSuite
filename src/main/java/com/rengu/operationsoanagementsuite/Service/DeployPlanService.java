package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.*;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.DeployPlanRepository;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import com.rengu.operationsoanagementsuite.Utils.Tools;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

@Service
public class DeployPlanService {

    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private DeployPlanRepository deployPlanRepository;
    @Autowired
    private DeployPlanDetailService deployPlanDetailService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private ComponentService componentService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UDPService udpService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 保存部署设计
    @Transactional
    public DeployPlanEntity saveDeployPlans(String projectId, DeployPlanEntity deployPlanEntity) {
        if (!projectService.hasProject(projectId)) {
            throw new CustomizeException(NotificationMessage.PROJECT_NOT_FOUND);
        }
        if (deployPlanEntity == null) {
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_NOT_FOUND);
        }
        if (StringUtils.isEmpty(deployPlanEntity.getName())) {
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_NAME_NOT_FOUND);
        }
        if (hasDeployPlans(deployPlanEntity.getName(), projectId)) {
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_EXISTS);
        }
        deployPlanEntity.setProjectEntity(projectService.getProject(projectId));
        deployPlanEntity.setLastModified(new Date());
        return deployPlanRepository.save(deployPlanEntity);
    }

    // 删除部署设计
    @Transactional
    public void deleteDeployPlans(String deployplanId) {
        if (!hasDeployPlans(deployplanId)) {
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_NOT_FOUND);
        }
        deployPlanRepository.delete(deployplanId);
    }

    // 更新部署设计
    @Transactional
    public DeployPlanEntity updateDeployPlans(String deployplanId, DeployPlanEntity deployPlanArgs) {
        if (!hasDeployPlans(deployplanId)) {
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_NOT_FOUND);
        }
        DeployPlanEntity deployPlanEntity = deployPlanRepository.findOne(deployplanId);
        BeanUtils.copyProperties(deployPlanArgs, deployPlanEntity, "id", "createTime", "lastModified", "deployPlanDetailEntity", "projectEntity");
        deployPlanEntity.setLastModified(new Date());
        return deployPlanRepository.save(deployPlanEntity);
    }

    // 查看部署设计
    @Transactional
    public DeployPlanEntity getDeployPlans(String deployplanId) {
        return deployPlanRepository.findOne(deployplanId);
    }

    @Transactional
    public List<DeployPlanEntity> getDeployPlans(String projectId, DeployPlanEntity deployPlanArgs) {
        return deployPlanRepository.findAll((root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (!StringUtils.isEmpty(projectId)) {
                predicateList.add(cb.equal(root.get("projectEntity").get("id"), projectId));
            }
            if (deployPlanArgs != null) {
                if (!StringUtils.isEmpty(deployPlanArgs.getName())) {
                    predicateList.add(cb.like(root.get("name"), deployPlanArgs.getName()));
                }
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        });
    }

    @Transactional
    public List<DeployPlanEntity> getDeployPlans(DeployPlanEntity deployPlanArgs) {
        return getDeployPlans(null, deployPlanArgs);
    }

    @Transactional
    public DeployPlanDetailEntity addDeployPlanDetail(String deployplanId, String deviceId, String componentId, String deployPath) {
        if (!hasDeployPlans(deployplanId)) {
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_NOT_FOUND);
        }
        if (!deviceService.hasDevice(deviceId)) {
            throw new CustomizeException(NotificationMessage.DEVICE_NOT_FOUND);
        }
        if (!componentService.hasComponent(componentId)) {
            throw new CustomizeException(NotificationMessage.COMPONENT_NOT_FOUND);
        }
        if (StringUtils.isEmpty(deployPath)) {
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_DEPLOY_PATH_NOT_FOUND);
        }
        DeployPlanEntity deployPlanEntity = deployPlanRepository.findOne(deployplanId);
        DeviceEntity deviceEntity = deviceService.getDevice(deviceId);
        ComponentEntity componentEntity = componentService.getComponent(componentId);
        DeployPlanDetailEntity deployPlanDetailEntity = deployPlanDetailService.saveDeployPlanDetails(deployPlanEntity, deviceEntity, componentEntity, deployPath);
        deployPlanEntity.setDeployPlanDetailEntities(addDeployPlanDetails(deployPlanEntity, deployPlanDetailEntity));
        deployPlanRepository.save(deployPlanEntity);
        return deployPlanDetailEntity;
    }

    @Transactional
    public void deleteDeployPlanDetails(String deployplandetailId) {
        deployPlanDetailService.deleteDeployPlanDetails(deployplandetailId);
    }

    @Transactional
    public DeployPlanDetailEntity updateDeployPlanDetails(String deployplandetailId, DeployPlanDetailEntity deployPlanDetailArgs) {
        return deployPlanDetailService.updateDeployPlanDetails(deployplandetailId, deployPlanDetailArgs);
    }

    @Transactional
    public List<DeployPlanDetailEntity> getDeployPlanDetails(String deployplanId, String deviceId) {
        return deployPlanDetailService.getDeployPlanDetails(deployplanId, deviceId);
    }


    // 开始部署
    @Transactional
    public Future<Boolean> startDeploy(String deployplanId, String deviceId) throws IOException, InterruptedException {
        if (!deployPlanRepository.exists(deployplanId)) {
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_NOT_FOUND);
        }
        if (!deviceService.hasDevice(deviceId)) {
            throw new CustomizeException(NotificationMessage.DEVICE_NOT_FOUND);
        }
        DeviceEntity deviceEntity = deviceService.getDevice(deviceId);
        List<DeployPlanDetailEntity> deployPlanDetailEntityList = deployPlanDetailService.getDeployPlanDetails(deployplanId, deviceId);
        return startDeploy(deviceEntity, deployPlanDetailEntityList);
    }

    // 异步发送文件
    @Async
    Future<Boolean> startDeploy(DeviceEntity deviceEntity, List<DeployPlanDetailEntity> deployPlanDetailEntities) throws IOException, InterruptedException {
        Socket socket = new Socket(deviceEntity.getIp(), deviceEntity.getTCPPort());
        if (socket.isConnected()) {
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            // 连接成功
            for (DeployPlanDetailEntity deployPlanDetailEntity : deployPlanDetailEntities) {
                ComponentEntity componentEntity = deployPlanDetailEntity.getComponentEntity();
                for (ComponentFileEntity componentFileEntity : componentEntity.getComponentFileEntities()) {
                    // 发送文件逻辑
                    dataOutputStream.write("fileRecvStart".getBytes());
                    // 1、发送文件路径 + 文件名
                    String deployPath = Tools.getString(deployPlanDetailEntity.getDeployPath() + componentFileEntity.getPath(), 255 - (deployPlanDetailEntity.getDeployPath() + componentFileEntity.getPath()).getBytes().length);
                    logger.info(deployPath + "字节长度：" + deployPath.getBytes().length);
                    dataOutputStream.write(deployPath.getBytes());
                    // 3、发送文件实体
                    IOUtils.copy(new FileInputStream(componentEntity.getFilePath() + componentFileEntity.getPath()), dataOutputStream);
                    // 4、单个文件发送结束标志
                    dataOutputStream.write("fileRecvEnd".getBytes());
                    Thread.sleep(5000);
                }
            }
            // 5、发送部署结束标志
            dataOutputStream.write("DeployEnd".getBytes());
            dataOutputStream.flush();
            dataOutputStream.close();
            socket.close();
            return new AsyncResult<>(true);
        }
        return new AsyncResult<>(false);
    }

    public List<DeviceScanResultEntity> scanDevices(String deployplanId, String deviceId) throws IOException, InterruptedException {
        // 检查部署设计id参数是否存在
        if (!hasDeployPlans(deployplanId)) {
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_NOT_FOUND);
        }
        // 检查设备id是否存在
        if (!deviceService.hasDevice(deviceId)) {
            throw new CustomizeException(NotificationMessage.DEVICE_EXISTS);
        }
        if (!deployPlanDetailService.hasDeployplandetail(deployplanId, deviceId)) {
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_DETAIL_NOT_FOUND);
        }
        List<DeployPlanDetailEntity> deployPlanDetailEntityList = deployPlanDetailService.getDeployPlanDetails(deployplanId, deviceId);
        List<DeviceScanResultEntity> deviceScanResultEntityList = new ArrayList<>();
        for (DeployPlanDetailEntity deployPlanDetailEntity : deployPlanDetailEntityList) {
            deviceScanResultEntityList.add(scanDevices(UUID.randomUUID().toString(), deployPlanDetailEntity));
        }
        return deviceScanResultEntityList;
    }

    public DeviceScanResultEntity scanDevices(String deployplanId, String deviceId, String componentId) throws IOException, InterruptedException {
        // 检查部署设计id参数是否存在
        if (!hasDeployPlans(deployplanId)) {
            throw new CustomizeException(NotificationMessage.DEPLOY_PLAN_NOT_FOUND);
        }
        // 检查设备id是否存在
        if (!deviceService.hasDevice(deviceId)) {
            throw new CustomizeException(NotificationMessage.DEVICE_EXISTS);
        }
        if (!componentService.hasComponent(componentId)) {
            throw new CustomizeException(NotificationMessage.COMPONENT_NOT_FOUND);
        }
        DeployPlanDetailEntity deployPlanDetailEntity = deployPlanDetailService.getDeployPlanDetails(deployplanId, deviceId, componentId);
        return scanDevices(UUID.randomUUID().toString(), deployPlanDetailEntity);
    }

    private DeviceScanResultEntity scanDevices(String id, DeployPlanDetailEntity deployPlanDetailEntity) throws IOException, InterruptedException {
        DeviceEntity deviceEntity = deployPlanDetailEntity.getDeviceEntity();
        udpService.sendScanDeviceMessage(deviceEntity.getIp(), deviceEntity.getUDPPort(), id, deployPlanDetailEntity);
        // 查询Redis中的存放的内容
        int count = 1;
        while (true) {
            if (stringRedisTemplate.hasKey(id)) {
                return deviceScanResultHandler(deployPlanDetailEntity, Tools.getJsonObject(stringRedisTemplate.opsForValue().get(id), DeviceScanResultEntity.class));
            } else {
                logger.info("等待客户端上报扫描结果，第" + count + "次重试。");
                Thread.sleep(10000);
                count = count + 1;
                if (count == 10) {
                    logger.info("请求已超时，请求放弃。");
                    return null;
                }
            }
        }
    }

    // 添加部署设计信息
    private List<DeployPlanDetailEntity> addDeployPlanDetails(DeployPlanEntity deployPlanEntity, DeployPlanDetailEntity... deployPlanDetailEntities) {
        List<DeployPlanDetailEntity> deployPlanDetailEntityList = deployPlanEntity.getDeployPlanDetailEntities();
        if (deployPlanDetailEntityList == null) {
            deployPlanDetailEntityList = new ArrayList<>();
        }
        for (DeployPlanDetailEntity deployPlanDetailEntity : deployPlanDetailEntities) {
            if (!deployPlanDetailEntityList.contains(deployPlanDetailEntity)) {
                deployPlanDetailEntityList.add(deployPlanDetailEntity);
            }
        }
        return deployPlanDetailEntityList;
    }

    private DeviceScanResultEntity deviceScanResultHandler(DeployPlanDetailEntity deployPlanDetailEntity, DeviceScanResultEntity deviceScanResultEntity) {

        ComponentEntity componentEntity = deployPlanDetailEntity.getComponentEntity();
        String deployPath = deployPlanDetailEntity.getDeployPath();

        List<ComponentFileEntity> correctComponentFiles = new ArrayList<>();
        List<ComponentFileEntity> modifyedComponentFiles = new ArrayList<>();
        List<ComponentFileEntity> unknownFiles = new ArrayList<>();

        for (ComponentFileEntity componentFileEntity : deviceScanResultEntity.getScanResult()) {
            String filePath = componentFileEntity.getPath().replace(deployPath, "");
            String md5 = componentFileEntity.getMD5();
            boolean exists = false;
            for (ComponentFileEntity temp : componentEntity.getComponentFileEntities()) {
                if (filePath.equals(temp.getPath().replace(File.separator, "/"))) {
                    exists = true;
                    if (md5.equals(temp.getMD5())) {
                        // 一致文件列表
                        correctComponentFiles.add(temp);
                        break;
                    } else {
                        // 不一致文件列表
                        modifyedComponentFiles.add(temp);
                        break;
                    }
                }
            }
            if (!exists) {
                unknownFiles.add(componentFileEntity);
            }
        }
        deviceScanResultEntity.setCorrectComponentFiles(correctComponentFiles);
        deviceScanResultEntity.setModifyedComponentFiles(modifyedComponentFiles);
        deviceScanResultEntity.setUnknownFiles(unknownFiles);
        return deviceScanResultEntity;
    }

    private boolean hasDeployPlans(String deployplanId) {
        return deployPlanRepository.exists(deployplanId);
    }

    private boolean hasDeployPlans(String name, String projectId) {
        return deployPlanRepository.findByNameAndProjectEntityId(name, projectId) != null;
    }
}
