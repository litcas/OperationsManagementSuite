package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.DeviceEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Service.DeviceService;
import com.rengu.operationsoanagementsuite.Utils.ResultEntity;
import com.rengu.operationsoanagementsuite.Utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/devices")
public class DeviceController {
    @Autowired
    private DeviceService deviceService;

    // 保存设备
    @PostMapping
    public ResultEntity saveDevice(@AuthenticationPrincipal UserEntity loginUser, DeviceEntity deviceEntity) throws MissingServletRequestParameterException {
        return ResultUtils.init(HttpStatus.CREATED, ResultUtils.HTTPRESPONSE, loginUser, deviceService.saveDevice(deviceEntity));
    }

    // 删除设备
    @DeleteMapping(value = "/{deviceId}")
    public ResultEntity deleteDevice(@AuthenticationPrincipal UserEntity loginUser, @PathVariable("deviceId") String deviceId) throws MissingServletRequestParameterException {
        deviceService.deleteDevice(deviceId);
        return ResultUtils.init(HttpStatus.NO_CONTENT, ResultUtils.HTTPRESPONSE, loginUser, "删除id为" + deviceId + "的设备成功。");
    }

    // 更新设备
    @PatchMapping(value = "/{deviceId}")
    public ResultEntity updateDevice(@AuthenticationPrincipal UserEntity loginUser, @PathVariable("deviceId") String deviceId, DeviceEntity deviceArgs) throws MissingServletRequestParameterException {
        return ResultUtils.init(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, deviceService.updateDevice(deviceId, deviceArgs));
    }

    // 查询设备
    @GetMapping(value = "/{deviceId}")
    public ResultEntity getDevice(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "deviceId") String deviceId) throws MissingServletRequestParameterException {
        return ResultUtils.init(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, deviceService.getDevice(deviceId));
    }

    // 查询设备
    @GetMapping
    public ResultEntity getDevice(@AuthenticationPrincipal UserEntity loginUser, DeviceEntity deviceArgs) {
        return ResultUtils.init(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, deviceService.getDevice(deviceArgs));
    }
}
