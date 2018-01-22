package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Service.SystemService;
import com.rengu.operationsoanagementsuite.Utils.ResultEntity;
import com.rengu.operationsoanagementsuite.Utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.SocketException;

@RestController
@RequestMapping(value = "/system")
public class SystemController {

    @Autowired
    private SystemService systemService;

    @GetMapping(value = "/systeminfo")
    public ResultEntity getSystemInfo(@AuthenticationPrincipal UserEntity loginUser) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, systemService.getSystemInfo());
    }

    @GetMapping(value = "/networks")
    public ResultEntity getNetworks(@AuthenticationPrincipal UserEntity loginUser) throws SocketException {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, systemService.getNetworks());
    }
}