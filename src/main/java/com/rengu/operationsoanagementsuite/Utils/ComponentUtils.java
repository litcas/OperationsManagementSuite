package com.rengu.operationsoanagementsuite.Utils;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class ComponentUtils {
    @Autowired
    private ServerConfiguration serverConfiguration;

    // 获取组件实体文件库路径
    private String getLibraryPath(ComponentEntity componentEntity) {
        return serverConfiguration.getLibraryPath() + componentEntity.getName() + serverConfiguration.getNameSeparator() + componentEntity.getVersion() + File.separator;
    }

    // 组件对象初始化
    public ComponentEntity componentInit(ComponentEntity componentEntity, UserEntity loginUser) {
        componentEntity.setFilePath(getLibraryPath(componentEntity));
        componentEntity.setDeleted(false);
        // 设置组件的拥有者为当前登录用户
        if (loginUser != null) {
            List<UserEntity> userEntities = new ArrayList<>();
            userEntities.add(loginUser);
            componentEntity.setUserEntities(userEntities);
        }
        return componentEntity;
    }
}