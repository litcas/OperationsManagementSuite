package com.rengu.operationsoanagementsuite.Utils;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Repository.UserRepository;
import com.rengu.operationsoanagementsuite.Service.RoleService;
import com.rengu.operationsoanagementsuite.Service.UserService;
import com.rengu.operationsoanagementsuite.Task.HearBeatTask;
import com.rengu.operationsoanagementsuite.Task.MessageReceiveTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Component
@Order(value = 2)
public class ServerInit implements CommandLineRunner {

    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private RoleService roleService;
    @Autowired
    private ServerConfiguration serverConfiguration;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private HearBeatTask hearBeatTask;
    @Autowired
    private MessageReceiveTask messageReceiveTask;

    @Override
    public void run(String... args) throws IOException {
        // 服务启动后执行一些初始化的工作
        // 调试环境获取组件库路径
        String libraryPath = Objects.requireNonNull(ClassUtils.getDefaultClassLoader().getResource("")).getPath();
        // 调试环境组件库路径
        if (libraryPath.endsWith("/target/classes/")) {
            serverConfiguration.setComponentLibraryPath(libraryPath.replace("classes/", ServerConfiguration.COMPONENT_LIBRARY_NAME + File.separatorChar));
        }
        // relese环境组件库路径(OSX)
        if (libraryPath.endsWith("!/BOOT-INF/classes!/")) {
            serverConfiguration.setComponentLibraryPath(new File(libraryPath.replace("!/BOOT-INF/classes!/", "")).getParent() + File.separatorChar + ServerConfiguration.COMPONENT_LIBRARY_NAME + File.separatorChar);
        }
        // 初始化角色
        if (roleService.getRolesByName(ServerConfiguration.USER_ROLE_NAME) == null) {
            logger.info("默认角色信息'" + ServerConfiguration.USER_ROLE_NAME + "'不存在，自动创建。");
            roleService.saveRoles(ServerConfiguration.USER_ROLE_NAME);
        }
        if (roleService.getRolesByName(ServerConfiguration.ADMIN_ROLE_NAME) == null) {
            logger.info("默认角色信息'" + ServerConfiguration.ADMIN_ROLE_NAME + "'不存在，自动创建。");
            roleService.saveRoles(ServerConfiguration.ADMIN_ROLE_NAME);
        }
        //初始化管理员用户
        if (userRepository.findByUsername(serverConfiguration.getDefultUsername()) == null) {
            logger.info("默认用户信息'" + serverConfiguration.getDefultUsername() + "'不存在，自动创建。");
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(serverConfiguration.getDefultUsername());
            userEntity.setPassword(serverConfiguration.getDefultPassword());
            userEntity.setRoleEntities(userService.addRoles(userEntity, roleService.getRolesByName(ServerConfiguration.ADMIN_ROLE_NAME)));
            userRepository.save(userEntity);
        }
        // 启动接收心跳报文
        hearBeatTask.receiveHearBeat();
        // 启动报文接收线程
        messageReceiveTask.messageReceive();
    }
}
