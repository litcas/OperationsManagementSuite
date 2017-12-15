package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Entity.ComponentEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Service.ComponentService;
import com.rengu.operationsoanagementsuite.Utils.ResultEntity;
import com.rengu.operationsoanagementsuite.Utils.ResultUtils;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping(value = "/components")
public class ComponentController {
    @Autowired
    private ComponentService componentService;

    // 保存组件
    @PostMapping
    public ResultEntity saveComponent(@AuthenticationPrincipal UserEntity loginUser, ComponentEntity componentEntity, @RequestParam(value = "addFilePath") String[] addFilePath, @RequestParam(value = "componentfile") MultipartFile[] multipartFiles) throws MissingServletRequestParameterException, IOException, NoSuchAlgorithmException {
        return ResultUtils.resultBuilder(HttpStatus.CREATED, ResultUtils.HTTPRESPONSE, loginUser, componentService.saveComponent(loginUser, componentEntity, addFilePath, multipartFiles));
    }

    // 删除组件
    @DeleteMapping(value = "/{componentId}")
    public ResultEntity deleteComponent(@AuthenticationPrincipal UserEntity loginUser, @PathVariable String componentId) throws MissingServletRequestParameterException {
        return ResultUtils.resultBuilder(HttpStatus.NO_CONTENT, ResultUtils.HTTPRESPONSE, loginUser, componentService.deleteComponent(componentId));
    }

    // 更新组件
    @PatchMapping(value = "/{componentId}")
    public ResultEntity updateComponents(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "componentId") String componentId, ComponentEntity componentArgs) throws MissingServletRequestParameterException {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, componentService.updateComponents(componentId, componentArgs));
    }

    // 查询组件
    @GetMapping(value = "/{componentId}")
    public ResultEntity getComponents(@AuthenticationPrincipal UserEntity loginUser, @PathVariable(value = "componentId") String componentId) throws MissingServletRequestParameterException {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, componentService.getComponents(componentId));
    }

    // 查询组件
    @GetMapping
    public ResultEntity getComponents(@AuthenticationPrincipal UserEntity loginUser, ComponentEntity componentArgs) {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, componentService.getComponents(componentArgs));
    }

    // 导入组件
    @PostMapping(value = "/import")
    public ResultEntity importComponents(@AuthenticationPrincipal UserEntity loginUser, @RequestParam(value = "importComponents") MultipartFile[] multipartFiles) throws IOException, MissingServletRequestParameterException, ZipException, NoSuchAlgorithmException {
        return ResultUtils.resultBuilder(HttpStatus.OK, ResultUtils.HTTPRESPONSE, loginUser, componentService.importComponents(loginUser, multipartFiles));
    }

    // 导出组件
    @GetMapping(value = "/export/{componentId}")
    public void exportComponents(HttpServletResponse httpServletResponse, @PathVariable(value = "componentId") String componentId) throws MissingServletRequestParameterException, IOException {
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
}