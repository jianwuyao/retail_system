package com.jwyao.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwyao.system.entity.ErrorLog;
import com.jwyao.system.mapper.ErrorLogMapper;
import com.jwyao.system.service.ErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ErrorLogServiceImpl extends ServiceImpl<ErrorLogMapper, ErrorLog> implements ErrorLogService {

    @Autowired
    private ErrorLogMapper errorLogMapper;

    @Override
    public List<ErrorLog> getErrorLogList() {
        return errorLogMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public void createErrorLog(ErrorLog errorLog) {
        errorLogMapper.insert(errorLog);
    }

    @Override
    public void deleteErrorLog(String id) {
        errorLogMapper.deleteById(id);
    }

    @Override
    public void updateErrorLog(ErrorLog errorLog) {
        errorLogMapper.updateById(errorLog);
    }

}
