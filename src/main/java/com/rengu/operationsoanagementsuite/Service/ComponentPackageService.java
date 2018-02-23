package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Entity.ComponentPackageEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.ComponentPackageRepository;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ComponentPackageService {

    @Autowired
    private ComponentPackageRepository componentPackageRepository;
    @Autowired
    private ComponentService componentService;

    @Transactional
    public ComponentPackageEntity saveComponentPackages(ComponentPackageEntity componentPackageArgs, String[] componentIds) {
        if (componentIds.length == 0) {
            throw new CustomizeException(NotificationMessage.COMPONENT_PACKAGE_COMPONENTIDS_NOT_FOUND);
        }
        if (hasNameAndVersion(componentPackageArgs.getName(), componentPackageArgs.getVersion())) {
            throw new CustomizeException(NotificationMessage.COMPONENT_PACKAGE_EXISTS);
        }
        ComponentPackageEntity componentPackageEntity = new ComponentPackageEntity();
        BeanUtils.copyProperties(componentPackageArgs, componentPackageEntity, "id", "createTime");
        for (String componentId : componentIds) {
            componentPackageEntity.setComponentEntities(addComponents(componentPackageEntity, componentService.getComponents(componentId)));
        }
        return componentPackageRepository.save(componentPackageEntity);
    }

    @Transactional
    public void deleteComponentPackages(String componentPackageId) {
        componentPackageRepository.delete(getComponentPackages(componentPackageId));
    }

    @Transactional
    public ComponentPackageEntity updateComponentPackages(String componentPackageId, ComponentPackageEntity componentPackageArgs, String[] componentIds) {
        if (hasNameAndVersion(componentPackageArgs.getName(), componentPackageArgs.getVersion())) {
            throw new CustomizeException(NotificationMessage.COMPONENT_PACKAGE_EXISTS);
        }
        if (componentIds.length == 0) {
            throw new CustomizeException(NotificationMessage.COMPONENT_NOT_FOUND);
        }
        ComponentPackageEntity componentPackageEntity = getComponentPackages(componentPackageId);
        BeanUtils.copyProperties(componentPackageArgs, componentPackageEntity, "id", "createTime", "componentEntities");
        for (String componentId : componentIds) {
            List<ComponentEntity> componentEntityList = new ArrayList<>();
            componentEntityList.add(componentService.getComponents(componentId));
            componentPackageEntity.setComponentEntities(componentEntityList);
        }
        return componentPackageRepository.save(componentPackageEntity);
    }

    @Transactional
    public List<ComponentPackageEntity> getComponentPackages() {
        return componentPackageRepository.findAll();
    }

    @Transactional
    public ComponentPackageEntity getComponentPackages(String componentPackageId) {
        if (!hasComponentPackages(componentPackageId)) {
            throw new CustomizeException(NotificationMessage.COMPONENT_PACKAGE_NOT_FOUND);
        }
        return componentPackageRepository.findOne(componentPackageId);
    }

    public List<ComponentEntity> addComponents(ComponentPackageEntity componentPackageEntity, ComponentEntity componentEntity) {
        List<ComponentEntity> componentEntityList = componentPackageEntity.getComponentEntities();
        if (componentEntityList == null) {
            componentEntityList = new ArrayList<>();
        }
        if (!componentEntityList.contains(componentEntity)) {
            componentEntityList.add(componentEntity);
        }
        return componentEntityList;
    }

    public boolean hasComponentPackages(String componentPackageId) {
        return componentPackageRepository.exists(componentPackageId);
    }

    public boolean hasNameAndVersion(String name, String version) {
        return componentPackageRepository.findByNameAndVersion(name, version) != null;
    }
}
