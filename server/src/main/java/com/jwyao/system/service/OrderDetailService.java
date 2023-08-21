package com.jwyao.system.service;

import com.jwyao.system.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService {

    List<OrderDetail> getOrderDetailList(Long orderId);

    void createOrderDetail(List<OrderDetail> orderDetails);

    void deleteOrderDetail(String orderId);

}
