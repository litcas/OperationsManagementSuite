package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Entity.ComponentFileEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.ComponentRepository;
import com.rengu.operationsoanagementsuite.Utils.CompressUtils;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import com.rengu.operationsoanagementsuite.Utils.Tools;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ComponentService {

    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ComponentRepository componentRepository;
    @Autowired
    private ComponentFileService componentFileService;
    @Autowired
    private ServerConfiguration serverConfiguration;

    // 新建组件
    @Transactional
    public ComponentEntity saveComponents(ComponentEntity componentEntity, MultipartFile[] multipartFiles) throws IOException {
        // 检查组件名称参数是否存在
        if (StringUtils.isEmpty(componentEntity.getName())) {
            throw new CustomizeException(NotificationMessage.COMPONENT_NAME_NOT_FOUND);
        }
        // 检查组件版本号参数是否存在
        if (StringUtils.isEmpty(componentEntity.getVersion())) {
            throw new CustomizeException(NotificationMessage.COMPONENT_VERSION_EXISTS);
        }
        // 检查组件是否存在
        if (hasComponent(componentEntity.getName(), componentEntity.getVersion())) {
            throw new CustomizeException(NotificationMessage.COMPONENT_EXISTS);
        }
        // 设置组件文件关联
        componentEntity.setFilePath(getEntityPath(componentEntity));
        List<ComponentFileEntity> componentFileEntityList = componentFileService.saveComponentFiles(componentEntity, multipartFiles);
        componentEntity.setComponentFileEntities(addComponentFile(componentEntity, componentFileEntityList));
        componentEntity.setSize(FileUtils.sizeOf(new File(componentEntity.getFilePath())));
        componentEntity.setDeleted(false);
        componentEntity.setLastModified(new Date());
        return componentRepository.save(componentEntity);
    }

    // 删除组件信息
    @Transactional
    public ComponentEntity deleteComponents(String componentId) {
        if (!hasComponent(componentId)) {
            throw new CustomizeException(NotificationMessage.COMPONENT_NOT_FOUND);
        }
        ComponentEntity componentEntity = getComponent(componentId);
        componentEntity.setDeleted(true);
        componentEntity.setLastModified(new Date());
        return componentRepository.save(componentEntity);
    }

    // 更新组件信息
    @Transactional
    public ComponentEntity updateComponents(String componentId, ComponentEntity componentArgs) {
        if (!hasComponent(componentId)) {
            throw new CustomizeException(NotificationMessage.COMPONENT_NOT_FOUND);
        }
        // 查询需要修改的组件
        ComponentEntity componentEntity = getComponent(componentId);
        BeanUtils.copyProperties(componentArgs, componentEntity, "id", "createTime", "componentFileEntities");
        // todo 添加对组件实体文件的修改功能
        // 设置组件大小
        componentEntity.setSize(FileUtils.sizeOf(new File(componentEntity.getFilePath())));
        componentEntity.setLastModified(new Date());
        componentRepository.save(componentEntity);
        return componentEntity;
    }

    // 根据id查询组件信息
    public ComponentEntity getComponent(String componentId) {
        return componentRepository.findOne(componentId);
    }

    // 查询所有组件
    public List<ComponentEntity> getComponents(ComponentEntity componentArgs) {
        return componentRepository.findAll((root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (componentArgs.getName() != null) {
                predicateList.add(cb.like(root.get("name"), componentArgs.getName()));
            }
            // 默认查询未删除的组件
            predicateList.add(cb.equal(root.get("deleted"), false));
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        });
    }

    // 导入组件实现
    @Transactional
    public List<ComponentEntity> importComponents(MultipartFile[] multipartFiles) throws IOException, ZipException {
        // 检查上传文件对象是否存在
        if (multipartFiles == null) {
            logger.info(NotificationMessage.COMPONENT_UPLOAD_FILE_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.COMPONENT_UPLOAD_FILE_NOT_FOUND);
        }
        List<ComponentEntity> componentEntityList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            // 在系统缓存文件中建立操作目录
            String id = UUID.randomUUID().toString();
            String tempFolderPath = FileUtils.getTempDirectoryPath() + id + File.separator;
            if (!new File(tempFolderPath).mkdirs()) {
                logger.info("在路径：" + tempFolderPath + "无法正确生成缓存文件夹，" + multipartFile.getOriginalFilename() + "导入失败");
                throw new FileNotFoundException("在路径：" + tempFolderPath + "无法正确生成缓存文件夹，" + multipartFile.getOriginalFilename() + "导入失败");
            }
            // 接受上传的文件
            File zipFile = new File(FileUtils.getTempDirectoryPath() + multipartFile.getOriginalFilename());
            FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), zipFile);
            // 1、解压缩文件到缓存目录
            File decompressFile = CompressUtils.decompressZip(zipFile, new File(tempFolderPath), null);
            // 2、解析json文件生成数据库目录
            File jsonFile = new File(tempFolderPath + ServerConfiguration.EXPORT_COMPONENT_INFO_NAME);
            if (!jsonFile.exists()) {
                logger.info("文件：" + tempFolderPath + ServerConfiguration.EXPORT_COMPONENT_INFO_NAME + "不存在，导出文件已损坏，" + multipartFile.getOriginalFilename() + "导入失败。");
                throw new FileNotFoundException("文件：" + tempFolderPath + ServerConfiguration.EXPORT_COMPONENT_INFO_NAME + "不存在，导出文件已损坏，" + multipartFile.getOriginalFilename() + "导入失败。");
            }
            ComponentEntity componentEntity = Tools.readJsonFile(jsonFile, ComponentEntity.class);
            // 检查组件是否存在
            if (componentRepository.findByNameAndVersion(componentEntity.getName(), componentEntity.getVersion()) != null) {
                logger.info("组件名称为：" + componentEntity.getName() + "版本号：" + componentEntity.getVersion() + "已存在，导入失败。");
                throw new CustomizeException("组件名称为：" + componentEntity.getName() + "版本号：" + componentEntity.getVersion() + "已存在，导入失败。");
            } else {
                // 组件库不存在该名称的组件
                componentEntity.setFilePath(getEntityPath(componentEntity));
                componentEntity.setDeleted(false);
                componentEntity.setComponentFileEntities(componentFileService.createComponentFile(componentEntity, decompressFile));
                // 设置组件大小
                componentEntity.setSize(FileUtils.sizeOf(new File(componentEntity.getFilePath())));
                componentEntity.setLastModified(new Date());
                componentRepository.save(componentEntity);
                componentEntityList.add(componentEntity);
            }
        }
        return componentEntityList;
    }

    // 导出组件实现
    public File exportComponents(String componentId) throws IOException {
        if (StringUtils.isEmpty(componentId)) {
            logger.info(NotificationMessage.COMPONENT_ID_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.COMPONENT_ID_NOT_FOUND);
        }
        if (!componentRepository.exists(componentId)) {
            logger.info(NotificationMessage.COMPONENT_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.COMPONENT_NOT_FOUND);
        }
        // 在系统缓存文件中建立操作目录
        String id = UUID.randomUUID().toString();
        String tempFolderPath = FileUtils.getTempDirectoryPath() + id + File.separator;
        if (!new File(tempFolderPath).mkdirs()) {
            logger.info("在路径：" + tempFolderPath + "无法正确生成缓存文件夹，导出失败");
            throw new FileNotFoundException("在路径：" + tempFolderPath + "无法正确生成缓存文件夹，导出失败");
        }
        ComponentEntity componentEntity = componentRepository.findOne(componentId);
        // 1.生成组件信息的json描述文件到缓存文件夹。
        Tools.writeJsonFile(componentEntity, new File(tempFolderPath + ServerConfiguration.EXPORT_COMPONENT_INFO_NAME));
        // 2.复制组件的实体文件到缓存文件夹。
        FileUtils.copyDirectory(new File(componentEntity.getFilePath()), new File(tempFolderPath + ServerConfiguration.EXPORT_ENTITY_FILE_NAME));
        // 3.压缩文件
        String zipFilePath = FileUtils.getTempDirectoryPath() + ServerConfiguration.EXPORT_COMPONENT_FILE_NAME + ".zip";
        return CompressUtils.compressToZip(tempFolderPath, zipFilePath);
    }

    // 获取组件实体文件库路径
    private String getEntityPath(ComponentEntity componentEntity) {
        if (StringUtils.isEmpty(componentEntity.getName()) || StringUtils.isEmpty(componentEntity.getVersion())) {
            logger.info(NotificationMessage.COMPONENT_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.COMPONENT_NOT_FOUND);
        }
        return serverConfiguration.getComponentLibraryPath() + componentEntity.getName() + ServerConfiguration.SEPARATOR + componentEntity.getVersion() + File.separatorChar;
    }

    private List<ComponentFileEntity> addComponentFile(ComponentEntity componentEntity, List<ComponentFileEntity> componentFileEntities) {
        List<ComponentFileEntity> componentFileEntityList = componentEntity.getComponentFileEntities();
        if (componentFileEntityList == null) {
            componentFileEntityList = new ArrayList<>();
        }
        for (ComponentFileEntity componentFileEntity : componentFileEntities) {
            if (!componentFileEntityList.contains(componentFileEntity)) {
                componentFileEntityList.add(componentFileEntity);
            }
        }
        return componentFileEntityList;
    }

    public boolean hasComponent(String componentId) {
        return componentRepository.exists(componentId);
    }

    private boolean hasComponent(String name, String version) {
        return componentRepository.findByNameAndVersion(name, version) != null;
    }

}
