package com.jwyao.system.service;

import com.jwyao.system.entity.Order;

import java.util.List;

public interface OrderService {

    List<Order> getOrderList();

    String createOrder(Order order);

    void deleteOrder(String id);

    void updateOrder(Order order);

    List<Order> getUserOrderList(Long userId, String status);

}
