package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.DeployLogEntity;
import com.rengu.operationsoanagementsuite.Entity.ResultEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Service.DeployLogService;
import com.rengu.operationsoanagementsuite.Utils.ResultUtils;
import com.sun.jmx.snmp.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping(value = "/log")
public class LogController {
    @Autowired
    private DeployLogService deployLogService;

    @GetMapping(value = "/search")
    public ResultEntity getLog(@AuthenticationPrincipal UserEntity loginUser, @RequestParam(value = "starttime") Long starttime, @RequestParam(value = "endtime") Long endtime, DeployLogEntity deployLogEntity) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startdate = null;
        Date enddate = null;
        if(starttime != null)
            startdate = new Date(starttime);
        if(endtime != null)
            enddate = new Date(endtime);

        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, deployLogService.getLogs(loginUser,startdate,enddate,deployLogEntity));
    }

}
