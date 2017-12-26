package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Entity.ComponentFileEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.ComponentFileRepository;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import com.rengu.operationsoanagementsuite.Utils.Tools;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ComponentFileService {
    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ComponentFileRepository componentFileRepository;

    @Transactional
    public List<ComponentFileEntity> createComponentFile(ComponentEntity componentEntity, MultipartFile[] multipartFiles, String[] addFilePath) throws IOException {
        // 检查上传文件对象是否存在
        if (multipartFiles == null) {
            logger.info(NotificationMessage.COMPONENT_UPLOAD_FILE_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.COMPONENT_UPLOAD_FILE_NOT_FOUND);
        }
        // 检查路径信息是否存在
        if (addFilePath == null) {
            logger.info(NotificationMessage.COMPONENT_UPLOAD_FILE_PATH_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.COMPONENT_UPLOAD_FILE_PATH_NOT_FOUND);
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
            File file = new File(componentEntity.getFilePath() + filePath + multipartFile.getOriginalFilename());
            FileUtils.copyFile(cacheFile, file);
            // 获取文件扩展名
            ComponentFileEntity componentFileEntity = new ComponentFileEntity();
            componentFileEntity.setName(file.getName());
            componentFileEntity.setMD5(Tools.getFileMD5(file));
            componentFileEntity.setType(FilenameUtils.getExtension(file.getName()));
            componentFileEntity.setSize(FileUtils.sizeOf(file));
            componentFileEntity.setPath(filePath + multipartFile.getOriginalFilename());
            componentFileEntityList.add(componentFileEntity);
        }
        return addComponentFile(componentEntity, componentFileEntityList);
    }

    @Transactional
    public List<ComponentFileEntity> createComponentFile(ComponentEntity componentEntity, File srcDir) throws IOException {
        File componentFile = new File(srcDir.getAbsolutePath() + File.separatorChar + ServerConfiguration.EXPORT_ENTITY_FILE_NAME);
        FileUtils.copyDirectory(componentFile, new File(componentEntity.getFilePath()));
        Collection<File> fileCollection = FileUtils.listFiles(new File(componentEntity.getFilePath()), null, true);
        List<ComponentFileEntity> componentFileEntityList = new ArrayList<>();
        for (File file : fileCollection) {
            ComponentFileEntity componentFileEntity = new ComponentFileEntity();
            componentFileEntity.setName(file.getName());
            componentFileEntity.setMD5(Tools.getFileMD5(file));
            componentFileEntity.setType(FilenameUtils.getExtension(file.getName()));
            componentFileEntity.setSize(FileUtils.sizeOf(file));
            componentFileEntity.setPath(file.getPath());
            componentFileEntityList.add(componentFileEntity);
        }
        return addComponentFile(componentEntity, componentFileEntityList);
    }

    private List<ComponentFileEntity> addComponentFile(ComponentEntity componentEntity, List<ComponentFileEntity> componentFileEntities) {
        if (componentEntity == null) {
            logger.info(NotificationMessage.COMPONENT_NOT_FOUND);
            throw new CustomizeException(NotificationMessage.COMPONENT_NOT_FOUND);
        }
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
}
