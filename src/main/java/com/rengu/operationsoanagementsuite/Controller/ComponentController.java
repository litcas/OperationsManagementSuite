package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Entity.ResultEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Service.ComponentService;
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
    public ResultEntity saveComponents(@AuthenticationPrincipal UserEntity loginUser, ComponentEntity componentEntity, @RequestParam(value = "componentfile") MultipartFile[] multipartFiles) throws IOException {
        return ResultUtils.resultBuilder(HttpStatus.CREATED, ResultUtils.HTTPRESPONSE, loginUser, componentService.saveComponents(componentEntity, multipartFiles));
    }

    // 删除组件
    @DeleteMapping(value = "/{componentId}")
    public ResultEntity deleteComponents(@AuthenticationPrincipal UserEntity loginUser, @PathVariable String componentId) {
        return ResultUtils.resultBuilder(HttpStatus.NO_CONTENT, ResultUtils.HTTPRESPONSE, loginUser, componentService.deleteComponents(componentId));
    }

    // 更新组件
    @PatchMapping(value = "/{componentId}")
    public ResultEntity updateComponents(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "componentId") String componentId, @RequestParam(value = "componentfile") MultipartFile[] multipartFiles) throws IOException {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, componentService.updateComponents(componentId, multipartFiles));
    }

    // 查询组件
    @GetMapping(value = "/{componentId}")
    public ResultEntity getComponents(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "componentId") String componentId) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, componentService.getComponent(componentId));
    }

    // 查询组件
    @GetMapping
    public ResultEntity getComponents(@AuthenticationPrincipal UserEntity loginUser, ComponentEntity componentArgs) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, componentService.getComponents(componentArgs));
    }

    // 导入组件
    @PostMapping(value = "/import")
    public ResultEntity importComponents(@AuthenticationPrincipal UserEntity loginUser, @RequestParam(value = "importComponents") MultipartFile[] multipartFiles) throws IOException, ZipException {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, componentService.importComponents(multipartFiles));
    }

    // 导出组件
    @GetMapping(value = "/export/{componentId}")
    public void exportComponents(HttpServletResponse httpServletResponse, @PathVariable(value = "componentId") String componentId) throws IOException {
        // 获取导出文件
        File exportComponents = componentService.exportComponents(componentId);
        // 设置请求相关信息
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

    @PostMapping(value = "/copy/{componentId}")
    public ResultEntity copyComponents(@AuthenticationPrincipal UserEntity loginUser, @PathVariable String componentId) throws IOException {
        return ResultUtils.resultBuilder(HttpStatus.CREATED, ResultUtils.HTTPRESPONSE, loginUser, componentService.copyComponents(componentId));
    }
}