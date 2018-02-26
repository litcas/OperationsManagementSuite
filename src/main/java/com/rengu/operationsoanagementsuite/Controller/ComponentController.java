package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Entity.ResultEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Service.ComponentService;
import com.rengu.operationsoanagementsuite.Utils.NotificationMessage;
import com.rengu.operationsoanagementsuite.Utils.ResultUtils;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;

@RestController
@RequestMapping(value = "/components")
public class ComponentController {

    @Autowired
    private ComponentService componentService;

    // 保存组件
    @PostMapping
    public ResultEntity saveComponents(@AuthenticationPrincipal UserEntity loginUser, ComponentEntity componentArgs, @RequestParam(value = "componentfiles") MultipartFile[] componentFiles) throws IOException {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.CREATED, componentService.saveComponents(componentArgs, componentFiles));
    }

    // 删除组件
    @DeleteMapping(value = "/{componentId}")
    public ResultEntity deleteComponents(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "componentId") String componentId) {
        componentService.deleteComponents(componentId);
        return ResultUtils.resultBuilder(loginUser, HttpStatus.NO_CONTENT, NotificationMessage.COMPONENT_DELETE);
    }

    // 修改组件
    @PostMapping(value = "/{componentId}/update")
    public ResultEntity updateComponents(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "componentId") String componentId, ComponentEntity componentArgs, @RequestParam(value = "componentfiles") MultipartFile[] componentFiles) throws IOException {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.NO_CONTENT, componentService.updateComponents(componentId, componentArgs, componentFiles));
    }

    // 查询所有组件
    @GetMapping
    public ResultEntity getComponents(@AuthenticationPrincipal UserEntity loginUser, @RequestParam(value = "isShowHistory") boolean isShowHistory) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, componentService.getComponents(isShowHistory));
    }

    // 查询组件
    @GetMapping(value = "/{componentId}")
    public ResultEntity getComponents(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "componentId") String componentId) {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, componentService.getComponents(componentId));
    }

    // 导出组件
    @GetMapping(value = "/{componentId}/export")
    public void exportComponents(@PathVariable(value = "componentId") String componentId, HttpServletResponse httpServletResponse) throws IOException {
        File exportComponents = componentService.exportComponents(componentId);
        //判断文件类型
        String mimeType = URLConnection.guessContentTypeFromName(exportComponents.getName());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        httpServletResponse.setContentType(mimeType);
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + new String(exportComponents.getName().getBytes("utf-8"), "ISO8859-1"));
        httpServletResponse.setContentLengthLong(exportComponents.length());
        // 文件流输出
        IOUtils.copy(new FileInputStream(exportComponents), httpServletResponse.getOutputStream());
        httpServletResponse.flushBuffer();
    }

    // 导入组件
    @PostMapping(value = "/import")
    public ResultEntity importComponents(@AuthenticationPrincipal UserEntity loginUser, @RequestParam(value = "importComponents") MultipartFile[] multipartFiles) throws IOException, ZipException {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.OK, componentService.importComponents(multipartFiles));
    }

    @PostMapping(value = "/{componentId}/copy")
    public ResultEntity copyComponents(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "componentId") String componentId) throws IOException {
        return ResultUtils.resultBuilder(loginUser, HttpStatus.CREATED, componentService.copyComponents(componentId));
    }
}