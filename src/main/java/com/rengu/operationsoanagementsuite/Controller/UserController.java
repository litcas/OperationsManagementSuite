package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Service.UserService;
import com.rengu.operationsoanagementsuite.Utils.ResponseEntity;
import com.rengu.operationsoanagementsuite.Utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 新建用户
    @PostMapping
    public ResponseEntity saveUser(@AuthenticationPrincipal UserEntity loginUser, UserEntity userArgs) {
        UserEntity userEntity = userService.saveUser(userArgs);
        return ResponseUtils.ok(ResponseUtils.HTTPRESPONSE, loginUser, userEntity);
    }

    // 删除用户
    @DeleteMapping(value = "/{userId}")
    public ResponseEntity deleteUser(@AuthenticationPrincipal UserEntity loginUser, @PathVariable String userId) {
        UserEntity userEntity = userService.deleteUser(userId);
        return ResponseUtils.ok(ResponseUtils.HTTPRESPONSE, loginUser, userEntity);
    }

    // todo 限制该接口只能通过管理员身份访问
    // 查询所有用户信息
    @GetMapping
    public ResponseEntity getUsers(@AuthenticationPrincipal UserEntity loginUser) {
        List<UserEntity> userEntityList = userService.getUsers();
        return ResponseUtils.ok(ResponseUtils.HTTPRESPONSE, loginUser, userEntityList);
    }

    //更具id查询用户信息
    @GetMapping(value = "/{userId}")
    public ResponseEntity getUser(@AuthenticationPrincipal UserEntity loginUser, @PathVariable String userId) {
        UserEntity userEntity = userService.getUser(userId);
        return ResponseUtils.ok(ResponseUtils.HTTPRESPONSE, loginUser, userEntity);
    }

    // 为指定用户绑定指定角色
    @PutMapping(value = "/{userId}/roles/{roleId}")
    public ResponseEntity assignRoleToUser(@AuthenticationPrincipal UserEntity loginUser, @PathVariable String userId, @PathVariable String roleId) {
        UserEntity userEntity = userService.assignRoleToUser(userId, roleId);
        return ResponseUtils.ok(ResponseUtils.HTTPRESPONSE, loginUser, userEntity);
    }
}