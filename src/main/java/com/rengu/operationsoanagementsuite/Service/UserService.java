package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import com.rengu.operationsoanagementsuite.Entity.RoleEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Repository.RoleRepository;
import com.rengu.operationsoanagementsuite.Repository.UserRepository;
import com.rengu.operationsoanagementsuite.Utils.CustomizeException;
import com.rengu.operationsoanagementsuite.Utils.ResponseCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ServerConfiguration serverConfiguration;
    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, ServerConfiguration serverConfiguration) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.serverConfiguration = serverConfiguration;
    }

    //  保存用户
    @Transactional
    public UserEntity saveUser(UserEntity userArgs) {
        //  检查是否输入用户名密码
        if (userArgs.getUsername().isEmpty() || userArgs.getPassword().isEmpty()) {
            logger.info("用户名或密码信息错误，保存失败。");
            throw new CustomizeException(ResponseCodeEnum.SAVEFAILED);
        }
        // 检查用户名是否已存在
        if (userRepository.findByUsername(userArgs.getUsername()) != null) {
            logger.info("用户信息名称 = '" + userArgs.getUsername() + "'已存在，保存失败。");
            throw new CustomizeException(ResponseCodeEnum.SAVEFAILED);
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(userArgs.getUsername());
        userEntity.setPassword(userArgs.getPassword());
        // 绑定默认角色
        RoleEntity roleEntity = roleRepository.findByRole(serverConfiguration.getDefultUserRole());
        if (roleEntity != null) {
            List<RoleEntity> roleEntityList = new ArrayList<>();
            roleEntityList.add(roleEntity);
            userEntity.setRoles(roleEntityList);
        }
        return userRepository.save(userEntity);
    }

    // 删除用户
    @Transactional
    public UserEntity deleteUser(String userId) {
        // 先查寻id是否存在对应的用户
        UserEntity userEntity = userRepository.findOne(userId);
        if (userEntity == null) {
            logger.info("用户信息Id = '" + userId + "'不存在，删除失败。");
            throw new CustomizeException(ResponseCodeEnum.DELETEFAILED);
        }
        // 删除用户信息
        userRepository.delete(userId);
        return userEntity;
    }

    //根据用户Id查询用户
    @Transactional
    public UserEntity getUser(String userId) {
        UserEntity userEntity = userRepository.findOne(userId);
        if (userEntity == null) {
            logger.info("用户信息Id = '" + userId + "'不存在，查询失败。");
            throw new CustomizeException(ResponseCodeEnum.QUERYFAILED);
        }
        return userEntity;
    }

    // 查询所有用户
    @Transactional
    public List<UserEntity> getUsers() {
        List<UserEntity> userEntityList = userRepository.findAll();
        if (userEntityList == null) {
            logger.info("查询所有用户信息失败");
            throw new CustomizeException(ResponseCodeEnum.QUERYFAILED);
        }
        return userEntityList;
    }

    // 用户绑定角色
    @Transactional
    public UserEntity assignRoleToUser(String userId, String roleId) {
        UserEntity userEntity = userRepository.findOne(userId);
        RoleEntity roleEntity = roleRepository.findOne(roleId);
        if (userEntity == null || roleEntity == null) {
            logger.info("用户名或密码信息错误，更新失败。");
            throw new CustomizeException(ResponseCodeEnum.UPDATEFAILED);
        }
        userEntity.getRoles().add(roleEntity);
        return userRepository.save(userEntity);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(s);
        if (userEntity == null) {
            throw new UsernameNotFoundException("Username Not Found");
        }
        return userEntity;
    }
}