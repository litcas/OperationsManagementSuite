package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.*;
import com.rengu.operationsoanagementsuite.Repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EventService {

    private static final int SAVE_EVENT = 1;
    private static final int DELETE_EVENT = 2;
    private static final int UPDATE_EVENT = 3;
    private static final int IMPORT_EVENT = 4;

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public EventEntity saveEvent(String username, int type, String title, String content) {
        EventEntity eventEntity = new EventEntity();
        eventEntity.setUsername(username);
        eventEntity.setType(type);
        eventEntity.setTitle(title);
        eventEntity.setContent(content);
        return eventRepository.save(eventEntity);
    }

    public List<EventEntity> getEvents() {
        return eventRepository.findAll();
    }

    public EventEntity saveComponentEvent(UserEntity loginUser, ComponentEntity componentArgs) {
        String title = "新建组件：" + componentArgs.getName() + "版本：" + componentArgs.getVersion();
        String content = "用户：" + loginUser.getUsername() + "新建组件：" + componentArgs.getName() + "保存路径：" + componentArgs.getFilePath();
        return saveEvent(loginUser.getUsername(), SAVE_EVENT, title, content);
    }

    public EventEntity deleteComponentEvent(UserEntity loginUser, ComponentEntity componentArgs) {
        String title = "删除组件：" + componentArgs.getName() + "版本：" + componentArgs.getVersion();
        String content = "用户：" + loginUser.getUsername() + "删除组件：" + componentArgs.getName() + "保存路径：" + componentArgs.getFilePath();
        return saveEvent(loginUser.getUsername(), DELETE_EVENT, title, content);
    }

    public EventEntity updateComponentEvent(UserEntity loginUser, ComponentEntity componentArgs) {
        String title = "更新组件：" + componentArgs.getName() + "版本：" + componentArgs.getVersion();
        String content = "用户：" + loginUser.getUsername() + "更新组件：" + componentArgs.getName() + "保存路径：" + componentArgs.getFilePath();
        return saveEvent(loginUser.getUsername(), UPDATE_EVENT, title, content);
    }

    public EventEntity importComponentEvent(UserEntity loginUser, List<ComponentEntity> componentEntityList) {
        String title = "导入组件";
        String content = "用户：" + loginUser.getUsername() + "导入组件数量：" + componentEntityList.size();
        return saveEvent(loginUser.getUsername(), IMPORT_EVENT, title, content);
    }

    public EventEntity saveDeviceEvent(UserEntity loginUser, DeviceEntity deviceArgs) {
        String title = "新建设备：" + deviceArgs.getName();
        String content = "用户：" + loginUser.getUsername() + "新建设备：" + deviceArgs.getName() + "IP：" + deviceArgs.getIp();
        return saveEvent(loginUser.getUsername(), SAVE_EVENT, title, content);
    }

    public EventEntity deleteDeviceEvent(UserEntity loginUser, DeviceEntity deviceArgs) {
        String title = "删除设备：" + deviceArgs.getName();
        String content = "用户：" + loginUser.getUsername() + "删除设备：" + deviceArgs.getName() + "IP：" + deviceArgs.getIp();
        return saveEvent(loginUser.getUsername(), DELETE_EVENT, title, content);
    }

    public EventEntity updateDeviceEvent(UserEntity loginUser, DeviceEntity deviceArgs) {
        String title = "更新设备：" + deviceArgs.getName();
        String content = "用户：" + loginUser.getUsername() + "更新设备：" + deviceArgs.getName() + "IP：" + deviceArgs.getIp();
        return saveEvent(loginUser.getUsername(), UPDATE_EVENT, title, content);
    }

    public EventEntity saveProjectEvent(UserEntity loginUser, ProjectEntity projectArgs) {
        String title = "新建工程：" + projectArgs.getName();
        String content = "用户：" + loginUser.getUsername() + "新建工程：" + projectArgs.getName();
        return saveEvent(loginUser.getUsername(), SAVE_EVENT, title, content);
    }

    public EventEntity deleteProjectEvent(UserEntity loginUser, ProjectEntity projectArgs) {
        String title = "删除工程：" + projectArgs.getName();
        String content = "用户：" + loginUser.getUsername() + "删除工程：" + projectArgs.getName();
        return saveEvent(loginUser.getUsername(), DELETE_EVENT, title, content);
    }

    public EventEntity updateProjectEvent(UserEntity loginUser, ProjectEntity projectArgs) {
        String title = "更新工程：" + projectArgs.getName();
        String content = "用户：" + loginUser.getUsername() + "更新工程：" + projectArgs.getName();
        return saveEvent(loginUser.getUsername(), UPDATE_EVENT, title, content);
    }
}
