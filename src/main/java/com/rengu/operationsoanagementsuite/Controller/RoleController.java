package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.ResultEntity;
import com.rengu.operationsoanagementsuite.Entity.RoleEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Service.RoleService;
import com.rengu.operationsoanagementsuite.Utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    // 查看角色信息（管理员）
    @GetMapping(value = "/admin/{roleId}")
    public ResultEntity getRole(@AuthenticationPrincipal UserEntity loginUser, @PathVariable String roleId) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, roleService.getRoles(roleId));
    }

    // 查看角色信息(管理员)
    @GetMapping(value = "/admin")
    public ResultEntity getRole(@AuthenticationPrincipal UserEntity loginUser, RoleEntity roleArgs) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, roleService.getRoles(roleArgs));
    }
}
