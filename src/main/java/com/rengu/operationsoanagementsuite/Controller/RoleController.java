package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.RoleEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Service.RoleService;
import com.rengu.operationsoanagementsuite.Utils.ResponseEntity;
import com.rengu.operationsoanagementsuite.Utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "roles")
public class RoleController {
    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // 获取所有角色信息
    @GetMapping
    public ResponseEntity getRoles(@AuthenticationPrincipal UserEntity loginUser) {
        List<RoleEntity> roleEntityList = roleService.getRoles();
        return ResponseUtils.ok(ResponseUtils.HTTPRESPONSE, loginUser, roleEntityList);
    }

    // 更绝角色id查询角色信息
    @GetMapping(value = "/{roleId}")
    public ResponseEntity getRole(@AuthenticationPrincipal UserEntity loginUser, @PathVariable String roleId) {
        RoleEntity roleEntity = roleService.getRoleById(roleId);
        return ResponseUtils.ok(ResponseUtils.HTTPRESPONSE, loginUser, roleEntity);
    }
}
