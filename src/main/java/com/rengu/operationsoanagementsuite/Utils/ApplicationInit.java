package com.rengu.operationsoanagementsuite.Utils;

import com.rengu.operationsoanagementsuite.Entity.RoleEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Service.RoleService;
import com.rengu.operationsoanagementsuite.Service.UserService;
import com.rengu.operationsoanagementsuite.Task.HeartbeatTask;
import com.rengu.operationsoanagementsuite.Task.TcpReceiveTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Component
@Order(value = 1)
public class ApplicationInit implements CommandLineRunner {

    private final RoleService roleService;
    private final UserService userService;
    private final ApplicationConfiguration applicationConfiguration;
    private final HeartbeatTask heartbeatTask;
    @Autowired
    private TcpReceiveTask tcpReceiveTask;

    @Autowired
    public ApplicationInit(RoleService roleService, UserService userService, ApplicationConfiguration applicationConfiguration, HeartbeatTask heartbeatTask) {
        this.roleService = roleService;
        this.userService = userService;
        this.applicationConfiguration = applicationConfiguration;
        this.heartbeatTask = heartbeatTask;
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws IOException {
        // 1、创建用户和管理员角色
        if (!roleService.hasRole(RoleService.USER_ROLE_NAME)) {
            RoleEntity roleEntity = new RoleEntity();
            roleEntity.setName(RoleService.USER_ROLE_NAME);
            roleService.saveRoles(roleEntity);
        }
        if (!roleService.hasRole(RoleService.ADMIN_ROLE_NAME)) {
            RoleEntity roleEntity = new RoleEntity();
            roleEntity.setName(RoleService.ADMIN_ROLE_NAME);
            roleService.saveRoles(roleEntity);
        }
        // 2、创建管理员用户
        if (!userService.hasUsername(applicationConfiguration.getDefultUsername())) {
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(applicationConfiguration.getDefultUsername());
            userEntity.setPassword(applicationConfiguration.getDefultPassword());
            userService.saveUsers(userEntity, roleService.getRoles(RoleService.USER_ROLE_NAME), roleService.getRoles(RoleService.ADMIN_ROLE_NAME));
        }
        // 3.初始化组件存储路径
        String libraryPath = Objects.requireNonNull(ClassUtils.getDefaultClassLoader().getResource("")).getPath();
        // 调试环境组件库路径
        if (libraryPath.endsWith("/target/classes/")) {
            applicationConfiguration.setComponentLibraryPath(libraryPath.replace("classes/", applicationConfiguration.getComponentLibraryName() + "/"));
        }
        // relese环境组件库路径
        if (libraryPath.endsWith("!/BOOT-INF/classes!/")) {
            if (libraryPath.startsWith("file:/")) {
                libraryPath = libraryPath.replace("file:/", "");
            }
            applicationConfiguration.setComponentLibraryPath(new File(libraryPath.replace("!/BOOT-INF/classes!/", "")).getParent() + "/" + applicationConfiguration.getComponentLibraryName() + "/");
        }
        heartbeatTask.HeartbeatHandler();
        tcpReceiveTask.TCPReceiver();
    }
}