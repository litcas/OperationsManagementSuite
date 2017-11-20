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

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(value = 2)
public class ServerInit implements CommandLineRunner {

    private final RoleService roleService;
    private final ServerConfiguration serverConfiguration;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ServerInit(RoleService roleService, ServerConfiguration serverConfiguration, RoleRepository roleRepository, UserRepository userRepository) {
        this.roleService = roleService;
        this.serverConfiguration = serverConfiguration;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 服务启动后执行一些初始化的工作
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
            userEntity.setRoles(roleEntityList);
            userRepository.save(userEntity);
        }
    }
}
