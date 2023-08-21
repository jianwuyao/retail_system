package com.jwyao.system.controller;

import com.jwyao.system.common.APIResponse;
import com.jwyao.system.common.ResponseCode;
import com.jwyao.system.entity.OperationLog;
import com.jwyao.system.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 登录与操作日志管理
 */
@RestController
@RequestMapping("/opLog")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(){
        List<OperationLog> list = operationLogService.getOpLogList();
        return new APIResponse(ResponseCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/loginLogList", method = RequestMethod.GET)
    public APIResponse loginLogList() {
        List<OperationLog> list = operationLogService.getLoginLogList();
        return new APIResponse(ResponseCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public APIResponse create(OperationLog opLog) {
        operationLogService.createOpLog(opLog);
        return new APIResponse(ResponseCode.SUCCESS, "创建成功");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            operationLogService.deleteOpLog(id);
        }
        return new APIResponse(ResponseCode.SUCCESS, "删除成功");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public APIResponse update(OperationLog opLog) {
        operationLogService.updateOpLog(opLog);
        return new APIResponse(ResponseCode.SUCCESS, "更新成功");
    }

}
