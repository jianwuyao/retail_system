package com.jwyao.system.controller;

import com.jwyao.system.common.APIResponse;
import com.jwyao.system.common.ResponseCode;
import com.jwyao.system.entity.ErrorLog;
import com.jwyao.system.service.ErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * 错误日志管理
 */
@RestController
@RequestMapping("/errorLog")
public class ErrorLogController {

    @Autowired
    private ErrorLogService errorLogService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list() {
        List<ErrorLog> list = errorLogService.getErrorLogList();
        return new APIResponse(ResponseCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public APIResponse create(ErrorLog errorLog) {
        errorLogService.createErrorLog(errorLog);
        return new APIResponse(ResponseCode.SUCCESS, "创建成功");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            errorLogService.deleteErrorLog(id);
        }
        return new APIResponse(ResponseCode.SUCCESS, "删除成功");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public APIResponse update(ErrorLog errorLog) throws IOException {
        errorLogService.updateErrorLog(errorLog);
        return new APIResponse(ResponseCode.SUCCESS, "更新成功");
    }

}
