package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.RoleEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.UserRepository;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleService roleService;

    /**
     * Locates the user based on the username. In the actual implementation, the search
     * may possibly be case sensitive, or case insensitive depending on how the
     * implementation instance is configured. In this case, the <code>UserDetails</code>
     * object that comes back may have a username that is of a different case than what
     * was actually requested..
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username);
        if (userEntity == null) {
            throw new UsernameNotFoundException("Username Not Found");
        }
        return userEntity;
    }

    @Transactional
    public UserEntity saveUsers(UserEntity userArgs, RoleEntity... roleEntities) {
        // 检查用户名是否存在
        if (StringUtils.isEmpty(userArgs.getUsername())) {
            throw new CustomizeException(NotificationMessage.USER_USERNAME_NOT_FOUND);
        }
        // 检查密码是否存在
        if (StringUtils.isEmpty(userArgs.getPassword())) {
            throw new CustomizeException(NotificationMessage.USER_PASSWORD_NOT_FOUND);
        }
        // 检查用户名是否存在
        if (hasUsername(userArgs.getUsername())) {
            throw new CustomizeException(NotificationMessage.USER_EXISTS);
        }
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userArgs, userEntity, "id", "createTime", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled", "roleEntities");
        userEntity.setPassword(new BCryptPasswordEncoder().encode(userEntity.getPassword()).trim());
        userEntity.setRoleEntities(addRoles(userEntity, roleEntities));
        return userRepository.save(userEntity);
    }

    @Transactional
    public UserEntity saveUsers(UserEntity userArgs, boolean isAdmin) {
        return isAdmin ? saveUsers(userArgs, roleService.getRoles(RoleService.USER_ROLE_NAME), roleService.getRoles(RoleService.ADMIN_ROLE_NAME)) : saveUsers(userArgs, roleService.getRoles(RoleService.USER_ROLE_NAME));
    }

    @Transactional
    public void deleteUsers(String userId) {
        if (!hasUser(userId)) {
            throw new CustomizeException(NotificationMessage.USER_NOT_FOUND);
        }
        userRepository.delete(userId);
    }

    @Transactional
    public UserEntity updateUsers(String userId, UserEntity userArgs) {
        // 检查用户是否存在
        if (!hasUser(userId)) {
            throw new CustomizeException(NotificationMessage.USER_NOT_FOUND);
        }
        // 检查密码是否存在
        if (StringUtils.isEmpty(userArgs.getPassword())) {
            throw new CustomizeException(NotificationMessage.USER_PASSWORD_NOT_FOUND);
        }
        UserEntity userEntity = userRepository.findOne(userId);
        BeanUtils.copyProperties(userArgs, userEntity, "id", "createTime", "username", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled", "roleEntities");
        return userRepository.save(userEntity);
    }

    @Transactional
    public List<UserEntity> getUsers() {
        return userRepository.findAll();
    }


    @Transactional
    public UserEntity getUsers(String userId) {
        if (!hasUser(userId)) {
            throw new CustomizeException(NotificationMessage.USER_NOT_FOUND);
        }
        return userRepository.findOne(userId);
    }

    @Transactional
    public String forgotPassword(String userId) {
        if (!hasUser(userId)) {
            throw new CustomizeException(NotificationMessage.USER_NOT_FOUND);
        }
        UserEntity userEntity = userRepository.findOne(userId);
        String password = UUID.randomUUID().toString();
        userEntity.setPassword(new BCryptPasswordEncoder().encode(password));
        userRepository.save(userEntity);
        return userEntity.getUsername() + "的密码已被重置为：" + password;
    }

    private List<RoleEntity> addRoles(UserEntity userEntity, RoleEntity... roleEntities) {
        List<RoleEntity> roleEntityList = userEntity.getRoleEntities();
        if (roleEntityList == null) {
            roleEntityList = new ArrayList<>();
        }
        for (RoleEntity roleEntity : roleEntities) {
            if (!roleEntityList.contains(roleEntity)) {
                roleEntityList.add(roleEntity);
            }
        }
        return roleEntityList;
    }

    public boolean hasUsername(String username) {
        return userRepository.findByUsername(username) != null;
    }

    private boolean hasUser(String userId) {
        return userRepository.exists(userId);
    }
}
