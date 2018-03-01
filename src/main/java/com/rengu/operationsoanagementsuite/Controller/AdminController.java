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
@RequestMapping(value = "/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    // 删除用户
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/users/{userId}")
    public ResultEntity deleteUsers(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "userId") String userId) {
        userService.deleteUsers(userId);
        return ResultUtils.resultBuilder(loginUser, HttpStatus.NO_CONTENT, NotificationMessage.USER_DETETE);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/users/{userId}/changepassword")
    public ResultEntity changePassword(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "userId") String userId, UserEntity userArgs) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.NO_CONTENT, userService.changePassword(userId, userArgs));
    }

    // 查询用户
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/users")
    public ResultEntity getUsers(@AuthenticationPrincipal UserEntity loginUser) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, userService.getUsers());
    }
}
