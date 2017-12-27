package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.DeviceEntity;
import com.rengu.operationsoanagementsuite.Entity.ResultEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Service.DeviceService;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import com.rengu.operationsoanagementsuite.Utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/devices")
public class DeviceController {
    @Autowired
    private DeviceService deviceService;

    // 保存设备
    @PostMapping
    public ResultEntity saveDevice(@AuthenticationPrincipal UserEntity loginUser, @RequestParam(value = "projectId") String projectId, DeviceEntity deviceEntity) {
        return ResultUtils.resultBuilder(HttpStatus.CREATED, ResultUtils.HTTPRESPONSE, loginUser, deviceService.saveDevice(projectId, deviceEntity));
    }

    // 删除设备
    @DeleteMapping(value = "/{deviceId}")
    public ResultEntity deleteDevice(@AuthenticationPrincipal UserEntity loginUser, @PathVariable("deviceId") String deviceId) {
        deviceService.deleteDevice(deviceId);
        return ResultUtils.resultBuilder(HttpStatus.NO_CONTENT, ResultUtils.HTTPRESPONSE, loginUser, NotificationMessage.deviceDeleteMessage(deviceId));
    }

    // 更新设备
    @PatchMapping(value = "/{deviceId}")
    public ResultEntity updateDevice(@AuthenticationPrincipal UserEntity loginUser, @PathVariable("deviceId") String deviceId, DeviceEntity deviceArgs) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, deviceService.updateDevice(deviceId, deviceArgs));
    }

    // 查询设备
    @GetMapping(value = "/{deviceId}")
    public ResultEntity getDevice(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deviceId") String deviceId) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, deviceService.getDevice(deviceId));
    }

    // 查询设备
    @GetMapping
    public ResultEntity getDevices(@AuthenticationPrincipal UserEntity loginUser, DeviceEntity deviceArgs) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, deviceService.getDevices(deviceArgs));
    }
}
