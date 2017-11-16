package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.ResponseEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Utils.ResponseUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tests")
public class TestController {
    @GetMapping
    public ResponseEntity getTest(@AuthenticationPrincipal UserEntity loginUser) {
        return ResponseUtils.ok(loginUser.getUsername(), "get请求成功！");
    }

    @PostMapping
    public ResponseEntity postTest(@AuthenticationPrincipal UserEntity loginUser) {
        return ResponseUtils.ok(loginUser.getUsername(), "post请求成功！");
    }
}
