package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.DeviceEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Service.DeviceService;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import com.rengu.operationsoanagementsuite.Utils.ResultEntity;
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

    // 删除设备
    @DeleteMapping(value = "/{deviceId}")
    public ResultEntity deleteDevices(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deviceId") String deviceId) {
        deviceService.deleteDevices(deviceId);
        return ResultUtils.resultBuilder(loginUser, HttpStatus.NO_CONTENT, NotificationMessage.DEVICE_DELETED);
    }

    // 更新设备
    @PatchMapping(value = "/{deviceId}")
    public ResultEntity updateDevices(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deviceId") String deviceId, DeviceEntity deviceArgs) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, deviceService.updateDevices(deviceId, deviceArgs));
    }

    // 查询设备
    @GetMapping(value = "/{deviceId}")
    public ResultEntity updateDevices(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deviceId") String deviceId) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, deviceService.getDevices(deviceId));
    }

    // 查询设备
    @GetMapping
    public ResultEntity updateDevices(@AuthenticationPrincipal UserEntity loginUser) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, deviceService.getDevices());
    }
}
