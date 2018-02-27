package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@RestController
public class CustomizeErrorController implements ErrorController {

    private static final String PATH = "/error";

    @Autowired
    private ErrorAttributes errorAttributes;

    /**
     * Returns the path of the error page.
     *
     * @return the error path
     */
    @Override
    public String getErrorPath() {
        return PATH;
    }

    @RequestMapping(value = PATH)
    public ResultEntity error(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Throwable throwable = getError(httpServletRequest);
        Principal principal = httpServletRequest.getUserPrincipal();
        return ResultUtils.resultBuilder(principal, HttpStatus.valueOf(httpServletResponse.getStatus()), throwable);
    }

    private Throwable getError(HttpServletRequest httpServletRequest) {
        RequestAttributes requestAttributes = new ServletRequestAttributes(httpServletRequest);
        return errorAttributes.getError(requestAttributes);
    }
}
