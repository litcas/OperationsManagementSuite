package com.rengu.operationsoanagementsuite.Utils;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import com.rengu.operationsoanagementsuite.Entity.RoleEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Repository.RoleRepository;
import com.rengu.operationsoanagementsuite.Repository.UserRepository;
import com.rengu.operationsoanagementsuite.Service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;

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
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;


    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        // 服务启动后执行一些初始化的工作
        logger.info(ClassUtils.getDefaultClassLoader().getResource("").getPath());
        String libraryPath = ClassUtils.getDefaultClassLoader().getResource("").getPath();
        // 调试环境获取组件库路径
        if (libraryPath.endsWith("/target/classes/")) {
            libraryPath = libraryPath.replace("classes/", "");
            serverConfiguration.setLibraryPath(libraryPath);
        }
        // 初始化角色
        if (roleRepository.findByRole(serverConfiguration.getDefultUserRole()) == null) {
            logger.info("系统默认角色信息'" + serverConfiguration.getDefultUserRole() + "'不存在，系统自动创建。");
            roleService.saveRole(serverConfiguration.getDefultUserRole());
        }
        if (roleRepository.findByRole(serverConfiguration.getDefultAdminRole()) == null) {
            logger.info("系统默认角色信息'" + serverConfiguration.getDefultAdminRole() + "'不存在，系统自动创建。");
            roleService.saveRole(serverConfiguration.getDefultAdminRole());
        }
        //初始化管理员用户
        if (userRepository.findByUsername(serverConfiguration.getDefultUserName()) == null) {
            logger.info("系统默认用户信息'" + serverConfiguration.getDefultUserName() + "'不存在，系统自动创建。");
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(serverConfiguration.getDefultUserName());
            userEntity.setPassword(serverConfiguration.getDefultPassword());
            List<RoleEntity> roleEntityList = new ArrayList<>();
            roleEntityList.add(roleService.getRoleByRole(serverConfiguration.getDefultAdminRole()));
            userEntity.setRoleEntities(roleEntityList);
            userRepository.save(userEntity);
        }
    }
}
