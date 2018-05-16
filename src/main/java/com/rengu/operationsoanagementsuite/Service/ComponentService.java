package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Configuration.ApplicationConfiguration;
import com.rengu.operationsoanagementsuite.Entity.ComponentDetailEntity;
import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.ComponentRepository;
import com.rengu.operationsoanagementsuite.Utils.CompressUtils;
import com.rengu.operationsoanagementsuite.Utils.JsonUtils;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ComponentService {

    private final ApplicationConfiguration applicationConfiguration;
    private final ComponentRepository componentRepository;
    private final ComponentDetailService componentDetailService;
    private final EventService eventService;

    @Autowired
    public ComponentService(ApplicationConfiguration applicationConfiguration, ComponentRepository componentRepository, ComponentDetailService componentDetailService, EventService eventService) {
        this.applicationConfiguration = applicationConfiguration;
        this.componentRepository = componentRepository;
        this.componentDetailService = componentDetailService;
        this.eventService = eventService;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ComponentEntity saveComponents(UserEntity loginUser, ComponentEntity componentArgs, MultipartFile[] componentFiles) throws IOException {
        if (StringUtils.isEmpty(componentArgs.getName())) {
            throw new CustomizeException(NotificationMessage.COMPONENT_NAME_NOT_FOUND);
        }
        if (StringUtils.isEmpty(componentArgs.getVersion())) {
            throw new CustomizeException(NotificationMessage.COMPONENT_VERSION_NOT_FOUND);
        }
        if (hasNameAndVersion(componentArgs.getName(), componentArgs.getVersion(), false)) {
            throw new CustomizeException(NotificationMessage.COMPONENT_EXISTS);
        }
        componentArgs.setDeployPath(getDeployPath(componentArgs));
        componentArgs.setFilePath(getFilePath(componentArgs, null));
        componentArgs.setSize(FileUtils.sizeOfDirectory(new File(componentArgs.getFilePath())));
        if (componentFiles.length != 0) {
            componentArgs.setComponentDetailEntities(addComponentDetails(componentArgs, componentDetailService.getComponentDetails(componentArgs, componentFiles)));
            componentArgs.setSize(FileUtils.sizeOfDirectory(new File(componentArgs.getFilePath())));
        }
        componentArgs.setDisplaySize(FileUtils.byteCountToDisplaySize(componentArgs.getSize()));
        eventService.saveComponentEvent(loginUser, componentArgs);
        return componentRepository.save(componentArgs);
    }

    public void deleteComponents(UserEntity loginUser, String componentId) {
        ComponentEntity componentEntity = getComponents(componentId);
        componentEntity.setDeleted(true);
        eventService.deleteComponentEvent(loginUser, componentEntity);
        componentRepository.save(componentEntity);
    }

    public ComponentEntity updateComponents(UserEntity loginUser, String componentId, String[] removeIds, ComponentEntity componentArgs, MultipartFile[] componentFiles) throws IOException {
        ComponentEntity componentEntity = getComponents(componentId);
        // 移除组件的实体文件
        if (removeIds != null) {
            if (removeIds.length != 0) {
                for (String id : removeIds) {
                    ComponentDetailEntity componentDetailEntity = componentDetailService.getComponentDetails(id);
                    // 组件是否包含该文件
                    if (componentEntity.getComponentDetailEntities().contains(componentDetailEntity)) {
                        // 删除是否成功
                        if (new File(componentEntity.getFilePath() + componentDetailEntity.getPath()).delete()) {
                            componentEntity.getComponentDetailEntities().remove(componentDetailEntity);
                        }
                    }
                }
            }
        }
        // 更新组件信息
        if (!StringUtils.isEmpty(componentArgs.getName()) || !StringUtils.isEmpty(componentArgs.getVersion()) || !StringUtils.isEmpty(componentArgs.getDescription())) {
            if (!componentEntity.getName().equals(componentArgs.getName()) || !componentEntity.getVersion().equals(componentArgs.getVersion())) {
                if (hasNameAndVersion(componentArgs.getName(), componentArgs.getVersion(), false)) {
                    throw new CustomizeException(NotificationMessage.COMPONENT_EXISTS);
                }
            }
            BeanUtils.copyProperties(componentArgs, componentEntity, "id", "createTime", "filePath", "size", "componentDetailEntities");
            if (componentFiles.length != 0) {
                componentEntity.setComponentDetailEntities(addComponentDetails(componentEntity, componentDetailService.getComponentDetails(componentEntity, componentFiles)));
                componentEntity.setSize(FileUtils.sizeOfDirectory(new File(componentEntity.getFilePath())));
            }
        }
        componentEntity.setDisplaySize(FileUtils.byteCountToDisplaySize(componentEntity.getSize()));
        eventService.updateComponentEvent(loginUser, componentEntity);
        return componentRepository.save(componentEntity);
    }

    public List<ComponentEntity> getComponents(boolean isShowHistory) {
        return isShowHistory ? componentRepository.findAll() : componentRepository.findByDeleted(false);
    }

    public ComponentEntity getComponents(String componentId) {
        if (!hasComponent(componentId)) {
            throw new CustomizeException(NotificationMessage.COMPONENT_NOT_FOUND);
        }
        return componentRepository.findOne(componentId);
    }

    public File exportComponents(String componentId) throws IOException {
        ComponentEntity componentEntity = getComponents(componentId);
        // 建立缓存文件夹
        String cacheDirPath = FileUtils.getTempDirectoryPath() + UUID.randomUUID().toString() + "/";
        File cacheDir = new File(cacheDirPath);
        if (cacheDir.mkdirs()) {
            // 1、写入Json文件
            JsonUtils.writeJsonFile(componentEntity, new File(cacheDirPath + applicationConfiguration.getJsonFileName()));
            // 2、复制实体文件到缓存目录
//            new File(cacheDirPath + componentEntity.getId() + "/").mkdir();
            FileUtils.copyDirectory(new File(componentEntity.getFilePath()), new File(cacheDirPath + componentEntity.getId() + "/"));
            // 3、压缩文件
            return CompressUtils.compress(cacheDir, new File(FileUtils.getTempDirectoryPath() + applicationConfiguration.getCompressFileName()));
        } else {
            throw new FileNotFoundException(NotificationMessage.CACHE_CREAT_FAILED);
        }
    }

    public List<ComponentEntity> importComponents(UserEntity loginUser, MultipartFile[] multipartFiles) throws IOException, ZipException {
        if (multipartFiles.length == 0) {
            throw new CustomizeException(NotificationMessage.COMPONENT_FILE_NOT_FOUND);
        }
        List<ComponentEntity> componentEntityList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            // 接收上传的文件
            File compressFile = new File(FileUtils.getTempDirectoryPath() + multipartFile.getOriginalFilename());
            FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), compressFile);
            // 解压缩文件
            File cacheDir = CompressUtils.decompress(compressFile, new File(FileUtils.getTempDirectoryPath() + UUID.randomUUID().toString() + "/"));
            ComponentEntity componentEntity = new ComponentEntity();
            BeanUtils.copyProperties(JsonUtils.readJsonFile(new File(cacheDir + "/" + applicationConfiguration.getJsonFileName()), ComponentEntity.class), componentEntity, "id", "createTime", "Path", "size", "deleted", "componentDetailEntities");
            if (hasNameAndVersion(componentEntity.getName(), componentEntity.getVersion(), false)) {
                throw new CustomizeException(NotificationMessage.COMPONENT_EXISTS);
            }
            componentEntity.setFilePath(getFilePath(componentEntity, null));
            componentEntity.setComponentDetailEntities(addComponentDetails(componentEntity, componentDetailService.getComponentDetails(componentEntity, new File(cacheDir.getAbsolutePath() + "/" + JsonUtils.readJsonFile(new File(cacheDir + "/" + applicationConfiguration.getJsonFileName()), ComponentEntity.class).getId()))));
            componentEntity.setSize(FileUtils.sizeOfDirectory(new File(componentEntity.getFilePath())));
            componentEntity.setDisplaySize(FileUtils.byteCountToDisplaySize(componentEntity.getSize()));
            componentEntityList.add(componentRepository.save(componentEntity));
        }
        eventService.importComponentEvent(loginUser, componentEntityList);
        return componentEntityList;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ComponentEntity copyComponents(String componentId) throws IOException {
        ComponentEntity componentArgs = getComponents(componentId);
        ComponentEntity componentEntity = new ComponentEntity();
        BeanUtils.copyProperties(componentArgs, componentEntity, "id", "createTime", "name", "filePath", "size", "deleted", "componentDetailEntities");
        // 设置组件名称-自动累加数字
        int i = 1;
        String name = componentArgs.getName() + "-副本(" + i + ")";
        while (hasNameAndVersion(name, componentArgs.getVersion(), false)) {
            i = i + 1;
            name = componentArgs.getName() + "-副本(" + i + ")";
        }
        componentEntity.setName(name);
        componentEntity.setDeployPath(getDeployPath(componentEntity));
        componentEntity.setFilePath(getFilePath(componentEntity, null));
        if (new File(componentArgs.getFilePath()).exists()) {
            componentEntity.setComponentDetailEntities(addComponentDetails(componentEntity, componentDetailService.getComponentDetails(componentEntity, new File(componentArgs.getFilePath()))));
            componentEntity.setSize(FileUtils.sizeOfDirectory(new File(componentEntity.getFilePath())));
        }
        componentEntity.setDisplaySize(FileUtils.byteCountToDisplaySize(componentEntity.getSize()));
        return componentRepository.save(componentEntity);
    }

    public List<ComponentDetailEntity> addComponentDetails(ComponentEntity componentEntity, List<ComponentDetailEntity> componentDetailEntities) {
        List<ComponentDetailEntity> componentDetailEntityList = componentEntity.getComponentDetailEntities();
        if (componentDetailEntityList == null) {
            componentDetailEntityList = new ArrayList<>();
        }
        for (ComponentDetailEntity componentDetailEntity : componentDetailEntities) {
            if (!componentDetailEntityList.contains(componentDetailEntity)) {
                componentDetailEntityList.add(componentDetailEntity);
            }
        }
        return componentDetailEntityList;
    }

    public String getFilePath(ComponentEntity componentEntity, String basePath) throws IOException {
        if (basePath == null) {
            basePath = "";
        }
        String componentPath = (applicationConfiguration.getComponentLibraryPath() + basePath + "/" + componentEntity.getId() + "/").replace("//", "/");
        FileUtils.forceMkdir(new File(componentPath));
        return componentPath;
    }

    public String getDeployPath(ComponentEntity componentEntity) {
        if (StringUtils.isEmpty(componentEntity.getDeployPath())) {
            throw new CustomizeException(NotificationMessage.COMPONENT_DEPLOY_PATH_NOT_FOUND);
        }
        // 替换斜线方向
        String deployPath = componentEntity.getDeployPath().replace("\\", "/");
        return deployPath.startsWith("/") ? deployPath : "/" + deployPath + "/" + componentEntity.getName() + "-" + componentEntity.getVersion();
    }

    public boolean hasNameAndVersion(String name, String version, boolean isDeleted) {
        return componentRepository.findByNameAndVersionAndAndDeleted(name, version, isDeleted).size() > 0;
    }

    public boolean hasComponent(String componentId) {
        return componentRepository.exists(componentId);
    }
}
