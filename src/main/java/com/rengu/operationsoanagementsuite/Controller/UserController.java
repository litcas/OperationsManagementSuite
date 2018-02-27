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

    // 新增用户
    @PostMapping
    public ResultEntity saveUsers(@AuthenticationPrincipal UserEntity loginUser, UserEntity userArgs, @RequestParam(value = "isAdmin") boolean isAdmin) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.CREATED, userService.saveUsers(userArgs, isAdmin));
    }

    // 删除用户
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{userId}")
    public ResultEntity deleteUsers(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "userId") String userId) {
        userService.deleteUsers(userId);
        return ResultUtils.resultBuilder(loginUser, HttpStatus.NO_CONTENT, NotificationMessage.USER_DETETE);
    }

    // 修改用户
    @PatchMapping(value = "/{userId}")
    public ResultEntity updateUsers(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "userId") String userId, UserEntity userArgs) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, userService.updateUsers(userId, userArgs));
    }

    // 查询用户
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResultEntity getUsers(@AuthenticationPrincipal UserEntity loginUser) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, userService.getUsers());
    }

    // 查询用户
    @GetMapping(value = "/{userId}")
    public ResultEntity getUsers(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "userId") String userId) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, userService.getUsers(userId));
    }

    // 用户登录
    @GetMapping(value = "/login")
    public ResultEntity login(@AuthenticationPrincipal UserEntity loginUser) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, loginUser);
    }

    // 忘记密码
    @GetMapping(value = "/forgotpassword/{userId}")
    public ResultEntity forgotPassword(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "userId") String userId) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, userService.forgotPassword(userId));
    }
}