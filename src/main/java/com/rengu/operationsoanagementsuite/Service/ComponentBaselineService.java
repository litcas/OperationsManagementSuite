package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.ComponentBaselineEntity;
import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.ComponentBaselineRepository;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ComponentBaselineService {

    @Autowired
    private ComponentBaselineRepository componentBaselineRepository;
    @Autowired
    private ComponentService componentService;

    @Transactional
    public ComponentBaselineEntity saveComponentBaselines(ComponentBaselineEntity componentBaselineArgs, String[] componentIds) {
        if (componentIds.length == 0) {
            throw new CustomizeException(NotificationMessage.COMPONENT_BASELINE_COMPONENTIDS_NOT_FOUND);
        }
        if (hasNameAndVersion(componentBaselineArgs.getName(), componentBaselineArgs.getVersion())) {
            throw new CustomizeException(NotificationMessage.COMPONENT_BASELINE_EXISTS);
        }
        ComponentBaselineEntity componentBaselineEntity = new ComponentBaselineEntity();
        BeanUtils.copyProperties(componentBaselineArgs, componentBaselineEntity, "id", "createTime");
        for (String componentId : componentIds) {
            componentBaselineEntity.setComponentEntities(addComponents(componentBaselineEntity, componentService.getComponents(componentId)));
        }
        return componentBaselineRepository.save(componentBaselineEntity);
    }

    @Transactional
    public void deleteComponentBaselines(String componentBaselineId) {
        componentBaselineRepository.delete(getComponentBaselines(componentBaselineId));
    }

    @Transactional
    public ComponentBaselineEntity updateComponentBaselines(String componentBaselineId, String[] componentIds) {
        ComponentBaselineEntity componentBaselineEntity = getComponentBaselines(componentBaselineId);
        for (String componentId : componentIds) {
            componentBaselineEntity.setComponentEntities(addComponents(componentBaselineEntity, componentService.getComponents(componentId)));
        }
        return componentBaselineRepository.save(componentBaselineEntity);
    }

    @Transactional
    public List<ComponentBaselineEntity> getComponentBaselines() {
        return componentBaselineRepository.findAll();
    }

    @Transactional
    public ComponentBaselineEntity getComponentBaselines(String componentBaselineId) {
        if (!hasComponentBaseline(componentBaselineId)) {
            throw new CustomizeException(NotificationMessage.COMPONENT_NOT_FOUND);
        }
        return componentBaselineRepository.findOne(componentBaselineId);
    }

    public List<ComponentEntity> addComponents(ComponentBaselineEntity componentBaselineEntity, ComponentEntity componentEntity) {
        List<ComponentEntity> componentEntityList = componentBaselineEntity.getComponentEntities();
        if (componentEntityList == null) {
            componentEntityList = new ArrayList<>();
        }
        if (!componentEntityList.contains(componentEntity)) {
            componentEntityList.add(componentEntity);
        }
        return componentEntityList;
    }

    public boolean hasComponentBaseline(String componentBaselineId) {
        return componentBaselineRepository.equals(componentBaselineId);
    }

    public boolean hasNameAndVersion(String name, String version) {
        return componentBaselineRepository.findByNameAndVersion(name, version) != null;
    }
}
