package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.RoleEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Service.RoleService;
import com.rengu.operationsoanagementsuite.Utils.ResultEntity;
import com.rengu.operationsoanagementsuite.Utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    // 获取所有角色信息
    @GetMapping
    public ResultEntity getRoles(@AuthenticationPrincipal UserEntity loginUser) {
        List<RoleEntity> roleEntityList = roleService.getRoles();
        return ResultUtils.init(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, roleEntityList);
    }

    // 更绝角色id查询角色信息
    @GetMapping(value = "/{roleId}")
    public ResultEntity getRole(@AuthenticationPrincipal UserEntity loginUser, @PathVariable String roleId) throws MissingServletRequestParameterException {
        RoleEntity roleEntity = roleService.getRoleById(roleId);
        return ResultUtils.init(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, roleEntity);
    }
}
