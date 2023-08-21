package com.jwyao.system.controller;

import com.jwyao.system.common.APIResponse;
import com.jwyao.system.common.ResponseCode;
import com.jwyao.system.entity.Order;
import com.jwyao.system.permission.Access;
import com.jwyao.system.permission.AccessLevel;
import com.jwyao.system.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 订单管理
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list() {
        List<Order> list = orderService.getOrderList();
        return new APIResponse(ResponseCode.SUCCESS, "查询成功", list);
    }


    @RequestMapping(value = "/userOrderList", method = RequestMethod.GET)
    public APIResponse userOrderList(Long userId, String status) {
        List<Order> list = orderService.getUserOrderList(userId, status);
        return new APIResponse(ResponseCode.SUCCESS, "查询成功", list);
    }

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public APIResponse create(Order order) {
        String msg = orderService.createOrder(order);
        return new APIResponse(ResponseCode.SUCCESS, "处理完成", msg);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @Transactional
    public APIResponse delete(String ids) {
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            orderService.deleteOrder(id);
        }
        return new APIResponse(ResponseCode.SUCCESS, "删除成功");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public APIResponse update(Order order) {
        orderService.updateOrder(order);
        return new APIResponse(ResponseCode.SUCCESS, "更新成功");
    }

    @RequestMapping(value = "/cancelOrder", method = RequestMethod.POST)
    public APIResponse cancelOrder(Long id) {
        Order order = new Order();
        order.setId(id);
        order.setStatus("7");
        orderService.updateOrder(order);
        return new APIResponse(ResponseCode.SUCCESS, "取消成功");
    }

    @RequestMapping(value = "/cancelUserOrder", method = RequestMethod.POST)
    public APIResponse cancelUserOrder(Long id) {
        Order order = new Order();
        order.setId(id);
        order.setStatus("7");
        orderService.updateOrder(order);
        return new APIResponse(ResponseCode.SUCCESS, "取消成功");
    }

}
