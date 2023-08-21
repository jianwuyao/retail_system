package com.jwyao.system.controller;

import com.jwyao.system.common.APIResponse;
import com.jwyao.system.common.ResponseCode;
import com.jwyao.system.entity.OrderDetail;
import com.jwyao.system.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 订单明细管理
 */
@RestController
@RequestMapping("/orderDetail")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(Long orderId) {
        List<OrderDetail> list = orderDetailService.getOrderDetailList(orderId);
        return new APIResponse(ResponseCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(@RequestBody List<OrderDetail> orderDetails) {
        orderDetailService.createOrderDetail(orderDetails);
        return new APIResponse(ResponseCode.SUCCESS, "创建成功");
    }

}
