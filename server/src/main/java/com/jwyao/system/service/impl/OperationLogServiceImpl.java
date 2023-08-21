package com.jwyao.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwyao.system.entity.OperationLog;
import com.jwyao.system.service.OperationLogService;
import com.jwyao.system.mapper.OperationLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    public List<OperationLog> getOpLogList() {
        QueryWrapper<OperationLog> queryWrapper = new QueryWrapper();
        queryWrapper.orderBy(true, false, "re_time");
        queryWrapper.last("limit 0, 1000");
        return operationLogMapper.selectList(queryWrapper);
    }

    @Override
    public void createOpLog(OperationLog opLog) {
        operationLogMapper.insert(opLog);
    }

    @Override
    public void deleteOpLog(String id) {
        operationLogMapper.deleteById(id);
    }

    @Override
    public void updateOpLog(OperationLog opLog) {
        operationLogMapper.updateById(opLog);
    }

    @Override
    public List<OperationLog> getLoginLogList() {
        QueryWrapper<OperationLog> queryWrapper = new QueryWrapper();
        queryWrapper.eq("re_url", "/api/user/userLogin");
        queryWrapper.orderBy(true, false, "re_time");
        queryWrapper.last("limit 0, 1000");
        return operationLogMapper.selectList(queryWrapper);
    }

}
