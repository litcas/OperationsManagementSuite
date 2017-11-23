package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Service.SystemService;
import com.rengu.operationsoanagementsuite.Utils.ResponseEntity;
import com.rengu.operationsoanagementsuite.Utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/system")
public class SystemController {

    private final SystemService systemService;

    @Autowired
    public SystemController(SystemService systemService) {
        this.systemService = systemService;
    }

    @GetMapping(value = "/info")
    public ResponseEntity getSystemInfo(@AuthenticationPrincipal UserEntity loginUser) {
        return ResponseUtils.ok(ResponseUtils.HTTPRESPONSE, loginUser, systemService.getSystemInfo());
    }

    @GetMapping(value = "/serverconfiguration")
    public ResponseEntity getServerConfiguration(@AuthenticationPrincipal UserEntity loginUser) {
        return ResponseUtils.ok(ResponseUtils.HTTPRESPONSE, loginUser, systemService.getServerConfiguration());
    }
}