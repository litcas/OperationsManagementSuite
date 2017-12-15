package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Service.UserService;
import com.rengu.operationsoanagementsuite.Utils.ResultEntity;
import com.rengu.operationsoanagementsuite.Utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 新建用户
    @PostMapping
    public ResultEntity saveUser(@AuthenticationPrincipal UserEntity loginUser, UserEntity userArgs) throws MissingServletRequestParameterException {
        UserEntity userEntity = userService.saveUser(userArgs);
        return ResultUtils.resultBuilder(HttpStatus.CREATED, ResultUtils.HTTPRESPONSE, loginUser, userEntity);
    }

    // 删除用户
    @DeleteMapping(value = "/{userId}")
    public ResultEntity deleteUser(@AuthenticationPrincipal UserEntity loginUser, @PathVariable String userId) throws MissingServletRequestParameterException {
        userService.deleteUser(userId);
        return ResultUtils.resultBuilder(HttpStatus.NO_CONTENT, ResultUtils.HTTPRESPONSE, loginUser, null);
    }

    // 查询所有用户信息
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResultEntity getUsers(@AuthenticationPrincipal UserEntity loginUser) {
        List<UserEntity> userEntityList = userService.getUsers();
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, userEntityList);
    }

    //更具id查询用户信息
    @GetMapping(value = "/{userId}")
    public ResultEntity getUser(@AuthenticationPrincipal UserEntity loginUser, @PathVariable String userId) throws MissingServletRequestParameterException {
        UserEntity userEntity = userService.getUser(userId);
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, userEntity);
    }

    // 为指定用户绑定指定角色
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{userId}/roles/{roleId}")
    public ResultEntity assignRoleToUser(@AuthenticationPrincipal UserEntity loginUser, @PathVariable String userId, @PathVariable String roleId) throws MissingServletRequestParameterException {
        UserEntity userEntity = userService.assignRoleToUser(userId, roleId);
        return ResultUtils.resultBuilder(HttpStatus.NO_CONTENT, ResultUtils.HTTPRESPONSE, loginUser, userEntity);
    }
}