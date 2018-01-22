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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
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
    public ComponentEntity saveComponents(ComponentEntity componentArgs, MultipartFile[] multipartFiles) throws IOException {
        if (StringUtils.isEmpty(componentArgs.getName())) {
            throw new CustomizeException(NotificationMessage.COMPONENT_NAME_NOT_FOUND);
        }
        if (StringUtils.isEmpty(componentArgs.getVersion())) {
            throw new CustomizeException(NotificationMessage.COMPONENT_VERSION_NOT_FOUND);
        }
        if (hasNameAndVersion(componentArgs.getName(), componentArgs.getVersion())) {
            throw new CustomizeException(NotificationMessage.COMPONENT_EXISTS);
        }
        componentArgs.setPath(getPath(componentArgs, null));
        componentArgs.setComponentDetailEntities(addComponentDetails(componentArgs, componentDetailService.getComponentDetails(componentArgs, multipartFiles)));
        componentArgs.setSize(getSize(componentArgs));
        return componentRepository.save(componentArgs);
    }

    @Transactional
    public void deleteComponents(String componentId) {
        if (!hasComponent(componentId)) {
            throw new CustomizeException(NotificationMessage.COMPONENT_NOT_FOUND);
        }
        ComponentEntity componentEntity = componentRepository.findOne(componentId);
        componentEntity.setDeleted(true);
        componentRepository.save(componentEntity);
    }

    @Transactional
    public List<ComponentEntity> getomponents(boolean isShowHistory) {
        return isShowHistory ? componentRepository.findAll() : componentRepository.findByDeleted(false);
    }

    @Transactional
    public ComponentEntity getomponents(String componentId) {
        if (!hasComponent(componentId)) {
            throw new CustomizeException(NotificationMessage.COMPONENT_NOT_FOUND);
        }
        return componentRepository.findOne(componentId);
    }

    @Transactional
    public File exportComponents(String componentId) throws IOException {
        if (!hasComponent(componentId)) {
            throw new CustomizeException(NotificationMessage.COMPONENT_NOT_FOUND);
        }
        ComponentEntity componentEntity = componentRepository.findOne(componentId);
        // 建立缓存文件夹
        String cacheDirPath = FileUtils.getTempDirectoryPath() + UUID.randomUUID().toString() + "/";
        File cacheDir = new File(cacheDirPath);
        if (cacheDir.mkdirs()) {
            // 1、写入Json文件
            JsonUtils.writeJsonFile(componentEntity, new File(cacheDirPath + applicationConfiguration.getJsonFileName()));
            // 2、复制实体文件到缓存目录
            FileUtils.copyDirectory(new File(applicationConfiguration.getComponentLibraryPath() + componentEntity.getPath()), new File(cacheDirPath + "/" + componentEntity.getName() + applicationConfiguration.getSeparator() + componentEntity.getVersion() + "/"));
            // 3、压缩文件
            String path = FileUtils.getTempDirectoryPath() + applicationConfiguration.getCompressFileName();
            return CompressUtils.compress(cacheDir, new File(path));
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
            componentEntity.setPath(getPath(componentEntity, null));
            componentEntity.setComponentDetailEntities(addComponentDetails(componentEntity, componentDetailService.getComponentDetails(componentEntity, new File(cacheDir + componentEntity.getPath()))));
            componentEntity.setSize(getSize(componentEntity));
            componentEntityList.add(componentRepository.save(componentEntity));
        }
        return componentEntityList;
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

    public String getPath(ComponentEntity componentEntity, String path) {
        if (path == null) {
            path = "";
        }
        return path + "/" + componentEntity.getName() + applicationConfiguration.getSeparator() + componentEntity.getVersion() + "/";
    }

    public long getSize(ComponentEntity componentEntity) {
        return FileUtils.sizeOfDirectory(new File(applicationConfiguration.getComponentLibraryPath() + componentEntity.getPath()));
    }

    public boolean hasNameAndVersion(String name, String version) {
        return componentRepository.findByNameAndVersion(name, version) != null;
    }

    public boolean hasComponent(String componentId) {
        return componentRepository.exists(componentId);
    }
}
