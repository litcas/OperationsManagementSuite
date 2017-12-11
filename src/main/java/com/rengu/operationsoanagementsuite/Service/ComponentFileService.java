package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Entity.ComponentFileEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.ComponentFileRepository;
import com.rengu.operationsoanagementsuite.Utils.ComponentUtils;
import com.rengu.operationsoanagementsuite.Utils.Tools;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ComponentFileService {
    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ComponentFileRepository componentFileRepository;
    @Autowired
    private ComponentUtils componentUtils;

    @Transactional
    public List<ComponentFileEntity> addComponentFile(String[] addFilePath, MultipartFile[] multipartFiles, ComponentEntity componentEntity) throws IOException, NoSuchAlgorithmException, MissingServletRequestParameterException {
        // 检查上传文件对象是否存在
        if (multipartFiles == null) {
            logger.info("请求参数解析异常：multipartFiles，保存失败。");
            throw new MissingServletRequestParameterException("multipartFiles", "MultipartFile[]");
        }
        // 检查路径信息是否存在
        if (addFilePath == null) {
            logger.info("请求参数解析异常：addFilePath，保存失败。");
            throw new MissingServletRequestParameterException("addFilePath", "String[]");
        }
        // 检查上传文件信息和文件是否对应
        if (multipartFiles.length != addFilePath.length) {
            logger.info("请求参数解析异常：MultipartFile与addFilePath信息不对应，保存失败。");
            throw new CustomizeException("请求参数解析异常：MultipartFile与addPath信息不对应，保存失败。");
        }
        List<ComponentFileEntity> componentFileEntityList = new ArrayList<>();
        for (int i = 0; i < multipartFiles.length; i++) {
            // 取相同位置的addFilePath和multipartFile
            MultipartFile multipartFile = multipartFiles[i];
            String filePath = addFilePath[i];
            // 在系统缓存文件夹建立文件
            File cacheFile = new File(FileUtils.getTempDirectoryPath() + multipartFile.getOriginalFilename());
            // 将文件输出到缓存文件夹
            FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), cacheFile);
            // 将缓存文件复制到库文件夹下
            File file = new File(componentUtils.getLibraryPath(componentEntity) + filePath + multipartFile.getOriginalFilename());
            FileUtils.copyFile(cacheFile, file);
            // 获取文件扩展名
            ComponentFileEntity componentFileEntity = new ComponentFileEntity();
            componentFileEntity.setName(file.getName());
            componentFileEntity.setMD5(Tools.getFileMD5(file));
            componentFileEntity.setType(FilenameUtils.getExtension(file.getName()));
            componentFileEntity.setSize(FileUtils.sizeOf(file));
            componentFileEntity.setPath(file.getPath());
            componentFileEntityList.add(componentFileEntity);
            componentFileRepository.save(componentFileEntity);
        }
        return componentFileEntityList;
    }
}
