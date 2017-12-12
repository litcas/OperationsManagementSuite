package com.rengu.operationsoanagementsuite.Utils;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class ComponentUtils {
    @Autowired
    private ServerConfiguration serverConfiguration;

    // 版本号更新
    public String versionUpdate(ComponentEntity componentEntity) {
        String[] strings = componentEntity.getVersion().split("\\.");
        int intNum = Integer.parseInt(strings[0]);
        int floatNum = Integer.parseInt(strings[1]) + 1;
        intNum = intNum + (int) Math.floor(floatNum / serverConfiguration.getStepper());
        floatNum = floatNum % serverConfiguration.getStepper();
        return intNum + "." + floatNum;
    }

    // 获取组件实体文件库路径
    public String getLibraryPath(ComponentEntity componentEntity) {
        return serverConfiguration.getLibraryPath() + componentEntity.getName() + serverConfiguration.getNameSeparator() + componentEntity.getVersion() + File.separatorChar;
    }
}