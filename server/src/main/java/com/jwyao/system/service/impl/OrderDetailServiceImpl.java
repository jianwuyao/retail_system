package com.jwyao.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwyao.system.entity.OrderDetail;
import com.jwyao.system.mapper.OrderDetailMapper;
import com.jwyao.system.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Override
    public List<OrderDetail> getOrderDetailList(Long orderId) {
        QueryWrapper<OrderDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        return orderDetailMapper.selectList(queryWrapper);
    }

    @Override
    public void createOrderDetail(List<OrderDetail> orderDetails) {
        orderDetailMapper.batchInsert(orderDetails);
    }

    @Override
    public void deleteOrderDetail(String orderId) {
        QueryWrapper<OrderDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        orderDetailMapper.delete(queryWrapper);
    }

}
