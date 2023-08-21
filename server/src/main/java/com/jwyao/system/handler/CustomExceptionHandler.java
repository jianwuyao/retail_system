package com.jwyao.system.handler;

import com.jwyao.system.common.APIResponse;
import com.jwyao.system.service.ErrorLogService;
import com.jwyao.system.common.ResponseCode;
import com.jwyao.system.entity.ErrorLog;
import com.jwyao.system.utils.HttpContextUtils;
import com.jwyao.system.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * 处理全局的Controller异常
 */
@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    @Autowired
    private ErrorLogService errorLogService;

    @ExceptionHandler(Exception.class)
    public APIResponse handleException(Exception ex) {
        saveLog(ex);
        log.error(ex.getMessage());
        return new APIResponse(ResponseCode.FAIL, ex.getMessage());
    }

    /**
     * 保存异常日志
     */
    private void saveLog(Exception ex){
        ErrorLog log = new ErrorLog();
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        if (request != null) {
            log.setIp(IpUtils.getIpAddr(request));
            log.setUrl(request.getRequestURI());
            log.setMethod(request.getMethod());
            log.setContent(Arrays.toString(ex.getStackTrace()).substring(0, 200));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            log.setLogTime(formatter.format(new Date()));
            errorLogService.createErrorLog(log);
        }
    }

}
