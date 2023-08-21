package com.jwyao.system.service;

import com.jwyao.system.entity.ErrorLog;

import java.util.List;

public interface ErrorLogService {

    List<ErrorLog> getErrorLogList();

    void createErrorLog(ErrorLog errorLog);

    void deleteErrorLog(String id);

    void updateErrorLog(ErrorLog errorLog);

}
