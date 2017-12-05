package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import com.rengu.operationsoanagementsuite.Entity.RoleEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Repository.RoleRepository;
import com.rengu.operationsoanagementsuite.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MissingServletRequestParameterException;

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
    public UserEntity saveUser(UserEntity userArgs) throws MissingServletRequestParameterException {
        UserEntity userEntity = new UserEntity();
        // 检查是否有用户名
        if (userArgs.getUsername() == null) {
            logger.info("请求参数解析异常：user.username不存在，保存失败。");
            throw new MissingServletRequestParameterException("user.username", "String");
        }
        userEntity.setUsername(userArgs.getUsername());
        //检查是否有密码
        if (userArgs.getPassword() == null) {
            logger.info("请求参数解析异常：user.password不存在，保存失败。");
            throw new MissingServletRequestParameterException("user.password", "String");
        }
        userEntity.setPassword(userArgs.getPassword());
        // 绑定默认角色
        RoleEntity roleEntity = roleRepository.findByRole(serverConfiguration.getDefultUserRole());
        if (roleEntity != null) {
            List<RoleEntity> roleEntityList = new ArrayList<>();
            roleEntityList.add(roleEntity);
            userEntity.setRoleEntities(roleEntityList);
        }
        return userRepository.save(userEntity);
    }

    // 删除用户
    @Transactional
    public void deleteUser(String userId) throws MissingServletRequestParameterException {
        if (userId == null) {
            logger.info("请求参数解析异常：user.id不存在，删除失败。");
            throw new MissingServletRequestParameterException("user.id", "String");
        }
        userRepository.delete(userId);
    }

    //根据用户Id查询用户
    @Transactional
    public UserEntity getUser(String userId) throws MissingServletRequestParameterException {
        if (userId == null) {
            logger.info("请求参数解析异常：user.id不存在，查询失败。");
            throw new MissingServletRequestParameterException("user.id", "String");
        }
        UserEntity userEntity = userRepository.findOne(userId);
        return userEntity;
    }

    // todo 管理员权限：查询所有用户信息
    @Transactional
    public List<UserEntity> getUsers() {
        List<UserEntity> userEntityList = userRepository.findAll();
        return userEntityList;
    }

    // 用户绑定角色
    @Transactional
    public UserEntity assignRoleToUser(String userId, String roleId) throws MissingServletRequestParameterException {
        if (userId == null) {
            logger.info("请求参数解析异常：user.id不存在，更新失败。");
            throw new MissingServletRequestParameterException("user.id", "String");
        }
        UserEntity userEntity = userRepository.findOne(userId);
        if (roleId == null) {
            logger.info("请求参数解析异常：role.id不存在，更新失败。");
            throw new MissingServletRequestParameterException("role.id", "String");
        }
        RoleEntity roleEntity = roleRepository.findOne(roleId);
        userEntity.getRoleEntities().add(roleEntity);
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