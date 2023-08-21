package com.jwyao.system.service;

import com.jwyao.system.entity.OperationLog;

import java.util.List;

public interface OperationLogService {

    List<OperationLog> getOpLogList();

    void createOpLog(OperationLog opLog);

    void deleteOpLog(String id);

    void updateOpLog(OperationLog opLog);

    List<OperationLog> getLoginLogList();

}
