package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Entity.ComponentFileEntity;
import com.rengu.operationsoanagementsuite.Repository.ComponentFileRepository;
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
    private ServerConfiguration serverConfiguration;

    @Transactional
    public List<ComponentFileEntity> saveComponentFile(MultipartFile[] multipartFiles, ComponentEntity componentEntity) throws IOException, NoSuchAlgorithmException {
        List<ComponentFileEntity> componentFileEntityList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            // 在系统缓存文件夹建立文件
            File cacheFile = new File(FileUtils.getTempDirectoryPath() + multipartFile.getOriginalFilename());
            // 将文件输出到缓存文件夹
            FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), cacheFile);
            // 将缓存文件复制到库文件夹下
            File file = new File(serverConfiguration.getLibraryPath() + componentEntity.getName() + "-" + componentEntity.getVersion() + "/" + multipartFile.getOriginalFilename());
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
