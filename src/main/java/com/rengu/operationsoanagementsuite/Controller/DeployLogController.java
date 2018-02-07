package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Service.DeployLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/deploylogs")
public class DeployLogController {

    @Autowired
    private DeployLogService deployLogService;
}
