package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.ResultEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Service.UserService;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import com.rengu.operationsoanagementsuite.Utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 保存用户
    @PostMapping
    public ResultEntity saveUsers(@AuthenticationPrincipal UserEntity loginUser, UserEntity userEntity) {
        return ResultUtils.resultBuilder(HttpStatus.CREATED, ResultUtils.HTTPRESPONSE, loginUser, userService.saveUsers(userEntity));
    }

    // 删除用户
    @DeleteMapping(value = "/{userId}")
    public ResultEntity deleteUsers(@AuthenticationPrincipal UserEntity loginUser, @PathVariable String userId) {
        userService.deleteUser(userId);
        return ResultUtils.resultBuilder(HttpStatus.NO_CONTENT, ResultUtils.HTTPRESPONSE, loginUser, NotificationMessage.userDeleteMessage(userId));
    }

    // 更新用户
    @PatchMapping(value = "/{userId}")
    public ResultEntity updateUsers(@AuthenticationPrincipal UserEntity loginUser, @PathVariable String userId, UserEntity userArgs) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, userService.updateUsers(userId, userArgs));
    }

    // 查看用户
    @GetMapping(value = "/{userId}")
    public ResultEntity getUser(@AuthenticationPrincipal UserEntity loginUser, @PathVariable String userId) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, userService.getUser(userId));
    }

    // 搜索用户信息
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/admin")
    public ResultEntity getUsers(@AuthenticationPrincipal UserEntity loginUser, UserEntity userArgs) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, userService.getUsers(userArgs));
    }

    // 为指定用户绑定指定角色
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{userId}/roles/{roleId}")
    public ResultEntity assignRoleToUser(@AuthenticationPrincipal UserEntity loginUser, @PathVariable String userId, @PathVariable String roleId) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, userService.assignRoleToUser(userId, roleId));
    }

    // 用户登录
    @PostMapping(value = "/login")
    public ResultEntity login(@AuthenticationPrincipal UserEntity loginUser) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, loginUser);
    }
}