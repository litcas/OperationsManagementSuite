package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Repository.ComponentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MissingServletRequestParameterException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ComponentService {

    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ComponentRepository componentRepository;

    // 新建组件
    @Transactional
    public ComponentEntity saveComponent(ComponentEntity componentArgs, UserEntity loginUser) throws MissingServletRequestParameterException {
        ComponentEntity componentEntity = new ComponentEntity();
        if (componentArgs.getName() == null) {
            logger.info("请求参数解析异常：component.name不存在，保存失败。");
            throw new MissingServletRequestParameterException("component.name", "String");
        }
        // 设置组件名称
        componentEntity.setName(componentArgs.getName());
        // 设置为最新版本
        componentEntity.setLatest(true);
        // 设置默认版本号
        componentEntity.setVersion("1.0");
        // 设置组件描述（非必须）
        if (componentArgs.getDescription() != null) {
            componentEntity.setDescription(componentArgs.getDescription());
        }
        // 设置组件的拥有者为当前登录用户
        List<UserEntity> userEntities = new ArrayList<>();
        userEntities.add(loginUser);
        componentEntity.setOwner(userEntities);
        return componentRepository.save(componentEntity);
    }
}
