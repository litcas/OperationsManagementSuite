package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.CabinEntity;
import com.rengu.operationsoanagementsuite.Entity.ResultEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Service.CabinService;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import com.rengu.operationsoanagementsuite.Utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/cabins")
public class CabinController {

    private final CabinService cabinService;

    @Autowired
    public CabinController(CabinService cabinService) {
        this.cabinService = cabinService;
    }

    @PostMapping
    public ResultEntity saveCabin(@AuthenticationPrincipal UserEntity loginUser, @RequestParam(value = "projectId") String projectId, CabinEntity cabinEntity) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.CREATED, cabinService.saveCabin(projectId, cabinEntity));
    }

    @DeleteMapping(value = "/{cabinId}")
    public ResultEntity deleteCabin(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "cabinId") String cabinId) {
        cabinService.deleteCabin(cabinId);
        return ResultUtils.resultBuilder(loginUser, HttpStatus.NO_CONTENT, NotificationMessage.CABIN_DELETED);
    }

    @PatchMapping(value = "/{cabinId}")
    public ResultEntity updateCabin(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "cabinId") String cabinId, CabinEntity cabinEntity) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, cabinService.updateCabin(cabinId, cabinEntity));
    }

    @GetMapping(value = "/{cabinId}")
    public ResultEntity getCabin(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "cabinId") String cabinId) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, cabinService.getCabin(cabinId));
    }

    @GetMapping
    public ResultEntity getCabin(@AuthenticationPrincipal UserEntity loginUser) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, cabinService.getCabin());
    }

    @PostMapping(value = "/{cabinId}/device/{deviceId}")
    public ResultEntity putDevice(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "cabinId") String cabinId, @PathVariable(value = "deviceId") String deviceId, @RequestParam(value = "name") String name, @RequestParam(value = "position") int position) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, cabinService.putDevice(cabinId, deviceId, name, position));
    }

    @DeleteMapping(value = "/{cabinId}/device/{deviceId}")
    public ResultEntity deleteDevice(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "cabinId") String cabinId, @PathVariable(value = "deviceId") String deviceId) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, cabinService.deleteDevice(cabinId, deviceId));
    }
}
