package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.ComponentRepository;
import com.rengu.operationsoanagementsuite.Utils.ComponentUtils;
import com.rengu.operationsoanagementsuite.Utils.CompressUtils;
import com.rengu.operationsoanagementsuite.Utils.Tools;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
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
    private ComponentUtils componentUtils;
    @Autowired
    private ServerConfiguration serverConfiguration;

    // 新建组件
    @Transactional
    public ComponentEntity saveComponent(UserEntity loginUser, ComponentEntity componentEntity, String[] addFilePath, MultipartFile[] multipartFiles) throws MissingServletRequestParameterException, IOException, NoSuchAlgorithmException {
        // 检查组件名称参数是否存在
        if (StringUtils.isEmpty(componentEntity.getName())) {
            logger.info("请求参数解析异常：component.name不存在，保存失败。");
            throw new MissingServletRequestParameterException("component.name", "String");
        }
        // 检查组件版本号参数是否存在
        if (StringUtils.isEmpty(componentEntity.getVersion())) {
            logger.info("请求参数解析异常：component.version，保存失败。");
            throw new MissingServletRequestParameterException("component.version", "String");
        }
        // 检查组件是否存在
        if (componentRepository.findByNameAndVersion(componentEntity.getName(), componentEntity.getVersion()) != null) {
            logger.info("名称为：" + componentEntity.getName() + "版本号：" + componentEntity.getVersion() + "的组件已存在，保存失败。");
            throw new DataIntegrityViolationException("名称为：" + componentEntity.getName() + "版本号：" + componentEntity.getVersion() + "的组件已存在，保存失败。");
        }
        componentEntity = componentUtils.componentInit(componentEntity, loginUser);
        // 设置组件文件关联
        componentEntity.setComponentFileEntities(componentFileService.addComponentFile(addFilePath, multipartFiles, componentEntity));
        // 设置组件大小
        componentEntity.setSize(FileUtils.sizeOf(new File(componentEntity.getFilePath())));
        return componentRepository.save(componentEntity);
    }

    // 删除组件信息
    @Transactional
    public ComponentEntity deleteComponent(String componentId) throws MissingServletRequestParameterException {
        if (StringUtils.isEmpty(componentId)) {
            logger.info("请求参数不正确：component.id不存在，删除失败。");
            throw new MissingServletRequestParameterException("component.id", "String");
        }
        if (componentRepository.exists(componentId)) {
            logger.info("请求参数不正确：id为：" + componentId + "的组件不存在，删除失败。");
            throw new CustomizeException("请求参数不正确：id为：" + componentId + "的组件不存在，删除失败。");
        }
        ComponentEntity componentEntity = componentRepository.findOne(componentId);
        componentEntity.setDeleted(true);
        componentEntity.setLastModified(new Date());
        return componentRepository.save(componentEntity);
    }

    // 更新组件信息
    @Transactional
    public ComponentEntity updateComponents(String componentId, ComponentEntity componentArgs) throws MissingServletRequestParameterException {
        if (StringUtils.isEmpty(componentId)) {
            logger.info("请求参数不正确：component.id不存在，更新失败。");
            throw new MissingServletRequestParameterException("component.id", "String");
        }
        if (componentRepository.exists(componentId)) {
            logger.info("请求参数不正确：id = " + componentId + "的组件不存在，更新失败。");
            throw new CustomizeException("请求参数不正确：id = " + componentId + "的组件不存在，更新失败。");
        }
        // 查询需要修改的组件
        ComponentEntity componentEntity = componentRepository.findOne(componentId);
        BeanUtils.copyProperties(componentArgs, componentEntity, "userEntities", "componentFileEntities");
        // todo 添加对组件实体文件的修改功能
        // 设置组件大小
        componentEntity.setSize(FileUtils.sizeOf(new File(componentEntity.getFilePath())));
        componentEntity.setLastModified(new Date());
        componentRepository.save(componentEntity);
        return componentEntity;
    }

    // 根据id查询组件信息
    public ComponentEntity getComponents(String componentId) throws MissingServletRequestParameterException {
        if (StringUtils.isEmpty(componentId)) {
            logger.info("请求参数不正确：component.id不存在，查询失败。");
            throw new MissingServletRequestParameterException("component.id", "String");
        }
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
    public List<ComponentEntity> importComponents(UserEntity loginUser, MultipartFile[] multipartFiles) throws MissingServletRequestParameterException, IOException, ZipException, NoSuchAlgorithmException {
        // 检查上传文件对象是否存在
        if (multipartFiles == null) {
            logger.info("请求参数解析异常：multipartFiles，保存失败。");
            throw new MissingServletRequestParameterException("multipartFiles", "MultipartFile[]");
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
            File jsonFile = new File(tempFolderPath + serverConfiguration.getExportDescriptionFileName() + ".json");
            if (!jsonFile.exists()) {
                logger.info("文件：" + tempFolderPath + serverConfiguration.getExportDescriptionFileName() + ".json" + "不存在，导出文件已损坏，" + multipartFile.getOriginalFilename() + "导入失败。");
                throw new FileNotFoundException("文件：" + tempFolderPath + serverConfiguration.getExportDescriptionFileName() + ".json" + "不存在，导出文件已损坏，" + multipartFile.getOriginalFilename() + "导入失败。");
            }
            ComponentEntity componentEntity = Tools.readJsonFile(jsonFile, ComponentEntity.class);
            // 检查组件是否存在
            if (componentRepository.findByNameAndVersion(componentEntity.getName(), componentEntity.getVersion()) != null) {
                logger.info("组件名称为：" + componentEntity.getName() + "版本号：" + componentEntity.getVersion() + "已存在，导入失败。");
                throw new CustomizeException("组件名称为：" + componentEntity.getName() + "版本号：" + componentEntity.getVersion() + "已存在，导入失败。");
            } else {
                // 组件库不存在该名称的组件
                componentEntity = componentUtils.componentInit(componentEntity, loginUser);
                componentEntity.setComponentFileEntities(componentFileService.addComponentFile(componentEntity, decompressFile));
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
    public File exportComponents(String componentId) throws MissingServletRequestParameterException, IOException {
        if (StringUtils.isEmpty(componentId)) {
            logger.info("请求参数不正确：component.id不存在，导出失败。");
            throw new MissingServletRequestParameterException("component.id", "String");
        }
        if (!componentRepository.exists(componentId)) {
            logger.info("请求参数不正确：id = " + componentId + "的组件不存在，导出失败。");
            throw new CustomizeException("请求参数不正确：id = " + componentId + "的组件不存在，导出失败。");
        }
        ComponentEntity componentEntity = componentRepository.findOne(componentId);
        // 在系统缓存文件中建立操作目录
        String id = UUID.randomUUID().toString();
        String tempFolderPath = FileUtils.getTempDirectoryPath() + id + File.separator;
        if (!new File(tempFolderPath).mkdirs()) {
            logger.info("在路径：" + tempFolderPath + "无法正确生成缓存文件夹，导出失败");
            throw new FileNotFoundException("在路径：" + tempFolderPath + "无法正确生成缓存文件夹，导出失败");
        }
        // 1.生成组件信息的json描述文件到缓存文件夹。
        Tools.writeJsonFile(componentEntity, new File(tempFolderPath + serverConfiguration.getExportDescriptionFileName() + ".json"));
        // 2.复制组件的实体文件到缓存文件夹。
        FileUtils.copyDirectory(new File(componentEntity.getFilePath()), new File(tempFolderPath + serverConfiguration.getExportComponentFileName()));
        // 3.压缩文件
        String zipFilePath = FileUtils.getTempDirectoryPath() + serverConfiguration.getExportFileName() + ".zip";
        return CompressUtils.compressToZip(tempFolderPath, zipFilePath);
    }
}
