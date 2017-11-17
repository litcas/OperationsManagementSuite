package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import com.rengu.operationsoanagementsuite.Entity.RoleEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Repository.RoleRepository;
import com.rengu.operationsoanagementsuite.Repository.UserRepository;
import com.rengu.operationsoanagementsuite.Utils.CustomizeException;
import com.rengu.operationsoanagementsuite.Utils.ResponseCodeEnum;
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

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, ServerConfiguration serverConfiguration) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.serverConfiguration = serverConfiguration;
    }

    //  保存用户
    @Transactional
    public UserEntity saveUser(UserEntity userArgs) {
        UserEntity userEntity = new UserEntity();
        //  检查是否输入用户名
        if (!userArgs.getUsername().isEmpty()) {
            // 检查是否用户名重复
            if (userRepository.findByUsername(userArgs.getUsername()) != null) {
                throw new CustomizeException(ResponseCodeEnum.USERREGISTERED);
            }
            userEntity.setUsername(userArgs.getUsername());
        }
        // 检查是否输入密码
        if (!userArgs.getPassword().isEmpty()) {
            userEntity.setPassword(userArgs.getPassword());
        }
        // 绑定默认角色
        RoleEntity roleEntity = roleRepository.findByRole(serverConfiguration.getDefultRole());
        if (roleEntity != null) {
            List<RoleEntity> roleEntityList = new ArrayList<>();
            roleEntityList.add(roleEntity);
            userEntity.setRoles(roleEntityList);
        }
        return userRepository.save(userEntity);
    }

    //根据用户Id查询用户
    @Transactional
    public UserEntity getUser(String userId) {
        UserEntity userEntity = userRepository.findOne(userId);
        if (userEntity == null) {
            throw new CustomizeException(ResponseCodeEnum.QUERYFAILED);
        }
        return userEntity;
    }

    // 查询所有用户
    @Transactional
    public List<UserEntity> getUsers() {
        List<UserEntity> userEntityList = userRepository.findAll();
        if (userEntityList == null) {
            throw new CustomizeException(ResponseCodeEnum.QUERYFAILED);
        }
        return userEntityList;
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