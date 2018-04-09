package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.ComponentDetailEntity;
import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.ComponentDetailRepository;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ComponentDetailService {

    @Autowired
    private ComponentDetailRepository componentDetailRepository;

    public List<ComponentDetailEntity> getComponentDetails(ComponentEntity componentEntity, MultipartFile[] multipartFiles) throws IOException {
        String cacheFilePath = FileUtils.getTempDirectoryPath() + UUID.randomUUID().toString() + "/";
        for (MultipartFile multipartFile : multipartFiles) {
            // 复制文件到缓存文件
            FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), new File(cacheFilePath + multipartFile.getOriginalFilename()));
        }
        return getComponentDetails(componentEntity, new File(cacheFilePath));
    }

    public List<ComponentDetailEntity> getComponentDetails(ComponentEntity componentEntity, File srcDir) throws IOException {
        Collection<File> fileCollection = FileUtils.listFiles(srcDir, null, true);
        List<ComponentDetailEntity> componentDetailEntityList = new ArrayList<>();
        for (File file : fileCollection) {
            // 从缓存文件中复制到组件库目录
            File componentFile = new File(componentEntity.getFilePath() + file.getAbsolutePath().replace(srcDir.getAbsolutePath(), "").replace("//", "/"));
            FileUtils.copyFile(file, componentFile);
            // 创建组件文件记录
            ComponentDetailEntity componentDetailEntity = new ComponentDetailEntity();
            componentDetailEntity.setName(file.getName());
            componentDetailEntity.setMD5(DigestUtils.md5Hex(new FileInputStream(file)));
            componentDetailEntity.setType(FilenameUtils.getExtension(file.getName()));
            componentDetailEntity.setSize(FileUtils.sizeOf(file));
            componentDetailEntity.setDisplaySize(FileUtils.byteCountToDisplaySize(componentDetailEntity.getSize()));
            componentDetailEntity.setPath(componentFile.getAbsolutePath().replace("\\", "/").replace(componentEntity.getFilePath().replace("\\", "/"), "/"));
            componentDetailEntityList.add(componentDetailEntity);
        }
        return componentDetailEntityList;
    }

    public ComponentDetailEntity getComponentDetails(String componentDetailId) {
        if (!hasComponentDetails(componentDetailId)) {
            throw new CustomizeException(NotificationMessage.COMPONENT_FILE_NOT_FOUND);
        }
        return componentDetailRepository.getOne(componentDetailId);
    }

    public boolean hasComponentDetails(String componentDetailId) {
        return componentDetailRepository.exists(componentDetailId);
    }
}