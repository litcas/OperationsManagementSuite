package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Entity.ComponentFileEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import com.rengu.operationsoanagementsuite.Utils.Tools;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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

    @Transactional
    public List<ComponentFileEntity> saveComponentFiles(ComponentEntity componentEntity, MultipartFile[] multipartFiles) throws IOException {
        // 检查上传文件对象是否存在
        if (multipartFiles == null) {
            throw new CustomizeException(NotificationMessage.COMPONENT_UPLOAD_FILE_NOT_FOUND);
        }
        List<ComponentFileEntity> componentFileEntityList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            // 在系统缓存文件夹建立文件
            File cacheFile = new File(FileUtils.getTempDirectoryPath() + multipartFile.getOriginalFilename());
            // 将文件输出到缓存文件夹
            FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), cacheFile);
            // 将缓存文件复制到库文件夹下
            File file = new File(componentEntity.getFilePath() + multipartFile.getOriginalFilename());
            FileUtils.copyFile(cacheFile, file);
            // 获取文件扩展名
            ComponentFileEntity componentFileEntity = new ComponentFileEntity();
            componentFileEntity.setName(file.getName());
            componentFileEntity.setMD5(Tools.getFileMD5(file));
            componentFileEntity.setType(FilenameUtils.getExtension(file.getName()));
            componentFileEntity.setSize(FileUtils.sizeOf(file));
            componentFileEntity.setPath(multipartFile.getOriginalFilename());
            componentFileEntityList.add(componentFileEntity);
        }
        return componentFileEntityList;
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
