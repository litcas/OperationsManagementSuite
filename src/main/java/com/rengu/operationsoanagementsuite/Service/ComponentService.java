package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.ComponentDetailEntity;
import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.ComponentRepository;
import com.rengu.operationsoanagementsuite.Utils.ApplicationConfiguration;
import com.rengu.operationsoanagementsuite.Utils.CompressUtils;
import com.rengu.operationsoanagementsuite.Utils.JsonUtils;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
public class ComponentService {

    @Autowired
    private ApplicationConfiguration applicationConfiguration;
    @Autowired
    private ComponentRepository componentRepository;
    @Autowired
    private ComponentDetailService componentDetailService;

    @Transactional
    public ComponentEntity saveComponents(ComponentEntity componentArgs, MultipartFile[] componentFiles) throws IOException {
        if (StringUtils.isEmpty(componentArgs.getName())) {
            throw new CustomizeException(NotificationMessage.COMPONENT_NAME_NOT_FOUND);
        }
        if (StringUtils.isEmpty(componentArgs.getVersion())) {
            throw new CustomizeException(NotificationMessage.COMPONENT_VERSION_NOT_FOUND);
        }
        if (hasNameAndVersion(componentArgs.getName(), componentArgs.getVersion())) {
            throw new CustomizeException(NotificationMessage.COMPONENT_EXISTS);
        }
        componentArgs.setDeployPath(getDeployPath(componentArgs));
        componentArgs.setFilePath(getFilePath(componentArgs, null));
        componentArgs.setComponentDetailEntities(addComponentDetails(componentArgs, componentDetailService.getComponentDetails(componentArgs, componentFiles)));
        componentArgs.setSize(getSize(componentArgs));
        return componentRepository.save(componentArgs);
    }

    @Transactional
    public void deleteComponents(String componentId) {
        ComponentEntity componentEntity = getComponents(componentId);
        componentEntity.setDeleted(true);
        componentRepository.save(componentEntity);
    }

    @Transactional
    public List<ComponentEntity> getComponents(boolean isShowHistory) {
        return isShowHistory ? componentRepository.findAll() : componentRepository.findByDeleted(false);
    }

    @Transactional
    public ComponentEntity getComponents(String componentId) {
        if (!hasComponent(componentId)) {
            throw new CustomizeException(NotificationMessage.COMPONENT_NOT_FOUND);
        }
        return componentRepository.findOne(componentId);
    }

    @Transactional
    public File exportComponents(String componentId) throws IOException {
        ComponentEntity componentEntity = getComponents(componentId);
        // 建立缓存文件夹
        String cacheDirPath = FileUtils.getTempDirectoryPath() + UUID.randomUUID().toString() + "/";
        File cacheDir = new File(cacheDirPath);
        if (cacheDir.mkdirs()) {
            // 1、写入Json文件
            JsonUtils.writeJsonFile(componentEntity, new File(cacheDirPath + applicationConfiguration.getJsonFileName()));
            // 2、复制实体文件到缓存目录
            FileUtils.copyDirectory(new File(componentEntity.getFilePath()), new File(cacheDirPath + "/" + componentEntity.getId() + "/"));
            // 3、压缩文件
            return CompressUtils.compress(cacheDir, new File(FileUtils.getTempDirectoryPath() + applicationConfiguration.getCompressFileName()));
        } else {
            throw new FileNotFoundException(NotificationMessage.CACHE_CREAT_FAILED);
        }
    }

    @Transactional
    public List<ComponentEntity> importComponents(MultipartFile[] multipartFiles) throws IOException, ZipException {
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
            if (hasNameAndVersion(componentEntity.getName(), componentEntity.getVersion())) {
                throw new CustomizeException(NotificationMessage.COMPONENT_EXISTS);
            }
            componentEntity.setFilePath(getFilePath(componentEntity, null));
            componentEntity.setComponentDetailEntities(addComponentDetails(componentEntity, componentDetailService.getComponentDetails(componentEntity, new File(cacheDir.getAbsolutePath() + "/" + JsonUtils.readJsonFile(new File(cacheDir + "/" + applicationConfiguration.getJsonFileName()), ComponentEntity.class).getId()))));
            componentEntity.setSize(getSize(componentEntity));
            componentEntityList.add(componentRepository.save(componentEntity));
        }
        return componentEntityList;
    }

    @Transactional
    public ComponentEntity copyComponents(String componentId) throws IOException {
        ComponentEntity componentArgs = getComponents(componentId);
        ComponentEntity componentEntity = new ComponentEntity();
        BeanUtils.copyProperties(componentArgs, componentEntity, "id", "createTime", "name", "filePath", "size", "deleted", "componentDetailEntities");
        // 设置组件名称-自动累加数字
        int i = 1;
        String name = componentArgs.getName() + "-副本( " + i + ")";
        while (hasNameAndVersion(name, componentArgs.getVersion())) {
            i = i + 1;
            name = componentArgs.getName() + "-副本( " + i + ")";
        }
        componentEntity.setName(name);
        componentEntity.setDeployPath(getDeployPath(componentEntity));
        componentEntity.setFilePath(getFilePath(componentEntity, null));
        componentEntity.setComponentDetailEntities(addComponentDetails(componentEntity, componentDetailService.getComponentDetails(componentEntity, new File(applicationConfiguration.getComponentLibraryPath() + componentArgs.getFilePath()))));
        componentEntity.setSize(getSize(componentEntity));
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

    public String getFilePath(ComponentEntity componentEntity, String basePath) {
        if (basePath == null) {
            basePath = "";
        }
        return (applicationConfiguration.getComponentLibraryPath() + basePath + "/" + componentEntity.getId() + "/").replace("//", "/");
    }

    public String getDeployPath(ComponentEntity componentEntity) {
        if (StringUtils.isEmpty(componentEntity.getDeployPath())) {
            throw new CustomizeException(NotificationMessage.COMPONENT_DEPLOY_PATH_NOT_FOUND);
        }
        // 替换斜线方向
        String deployPath = componentEntity.getDeployPath().replace("\\", "/");
        return deployPath.startsWith("/") ? deployPath : "/" + deployPath + "/" + componentEntity.getName() + "-" + componentEntity.getVersion();
    }

    public long getSize(ComponentEntity componentEntity) {
        return FileUtils.sizeOfDirectory(new File(componentEntity.getFilePath()));
    }

    public boolean hasNameAndVersion(String name, String version) {
        return componentRepository.findByNameAndVersion(name, version) != null;
    }

    public boolean hasComponent(String componentId) {
        return componentRepository.exists(componentId);
    }
}
