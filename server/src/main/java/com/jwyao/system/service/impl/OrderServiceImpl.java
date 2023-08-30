package com.jwyao.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwyao.system.common.BaseContext;
import com.jwyao.system.entity.*;
import com.jwyao.system.factory.IMessage;
import com.jwyao.system.factory.MessageFactory;
import com.jwyao.system.mapper.ThingMapper;
import com.jwyao.system.service.*;
import com.jwyao.system.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ThingMapper thingMapper;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ThingService thingService;

    @Autowired
    private UserService userService;

    @Override
    public List<Order> getOrderList() {
        List<Order> orderList = orderMapper.getList();
        return orderList.stream().peek((order -> {
            List<OrderDetail> orderDetails = orderDetailService.getOrderDetailList(order.getId());
            order.setOrderDetails(orderDetails);
        })).collect(Collectors.toList());
    }

    @Override
    public String createOrder(Order order) {
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            return "请重新登录";
        }
        List<ShoppingCart> shoppingCartList = shoppingCartService.getShoppingCartList(userId);
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            return "购物车为空";
        } else {
            order.setOrderTime(LocalDateTime.now());
            order.setStatus("1");
            orderMapper.insert(order);
            // 解决库存超卖问题
            List<OrderDetail> orderDetails = new ArrayList<>();
            Map<Long, Integer> thingCount = new HashMap<>();
            HashOperations<String, String, String> ops = stringRedisTemplate.opsForHash();
            for (ShoppingCart shoppingCart :shoppingCartList) {
                Long thingId = shoppingCart.getThingId();
                if (ops.get("thingRepertory", String.valueOf(thingId)) == null) {
                    Thing thing = thingService.getThingById(thingId);
                    ops.put("thingRepertory", String.valueOf(thingId), String.valueOf(thing.getRepertory()));
                }
                String thingRepertory = ops.get("thingRepertory", String.valueOf(thingId));
                if (thingRepertory != null && Integer.parseInt(thingRepertory) > 0) {
                    // 减Redis库存
                    thingCount.put(thingId, shoppingCart.getCount());
                    ops.increment("thingRepertory", String.valueOf(thingId), -shoppingCart.getCount());
                    thingRepertory = ops.get("thingRepertory", String.valueOf(thingId));
                    if (thingRepertory != null && Integer.parseInt(thingRepertory) >= 0) {
                        // 减MySQL库存
                        thingMapper.updateThingRepertory(thingId, shoppingCart.getCount());
                        OrderDetail orderDetail = new OrderDetail();
                        orderDetail.setThingId(thingId);
                        orderDetail.setClassificationId(shoppingCart.getClassificationId());
                        orderDetail.setOrderId(order.getId());
                        orderDetail.setTitle(shoppingCart.getTitle());
                        orderDetail.setPrice(shoppingCart.getPrice());
                        orderDetail.setNumber(String.valueOf(shoppingCart.getCount()));
                        orderDetails.add(orderDetail);
                    } else {
                        // 回退库存
                        for (OrderDetail orderDetail : orderDetails) {
                            thingMapper.updateThingRepertory(orderDetail.getThingId(), -Integer.parseInt(orderDetail.getNumber()));
                        }
                        for (Map.Entry<Long, Integer> entry : thingCount.entrySet()) {
                            Thing thing = thingMapper.selectById(entry.getKey());
                            ops.put("thingRepertory", String.valueOf(entry.getKey()), String.valueOf(thing.getRepertory()));
                        }
                        orderMapper.deleteById(order.getId());
                        return "【" + shoppingCart.getTitle() + "】已售罄，请您稍后重试";
                    }
                } else {
                    // 回退库存
                    for (OrderDetail orderDetail : orderDetails) {
                        thingMapper.updateThingRepertory(orderDetail.getThingId(), -Integer.parseInt(orderDetail.getNumber()));
                    }
                    for (Map.Entry<Long, Integer> entry : thingCount.entrySet()) {
                        Thing thing = thingMapper.selectById(entry.getKey());
                        ops.put("thingRepertory", String.valueOf(entry.getKey()), String.valueOf(thing.getRepertory()));
                    }
                    orderMapper.deleteById(order.getId());
                    return "【" + shoppingCart.getTitle() + "】已售罄，请您稍后重试";
                }
            }
            orderDetailService.createOrderDetail(orderDetails);
            return "success" + order.getId();
        }
    }

    @Override
    public void deleteOrder(String id) {
        orderMapper.deleteById(id);
        orderDetailService.deleteOrderDetail(id);
    }

    @Override
    public void updateOrder(Order order) {
        String status = order.getStatus();
        IMessage message = MessageFactory.getInstance("email");
        if ("2".equals(status)) {
            order.setPayTime(LocalDateTime.now());
            if (message != null) {
                Object msg = message.createMsg(order.getId(), status);
                if (msg != null) {
                    message.sendMsg(msg);
                }
            }
        } else if ("7".equals(status)) {
            // 回退库存
            HashOperations<String, String, String> ops = stringRedisTemplate.opsForHash();
            List<OrderDetail> orderDetails = orderDetailService.getOrderDetailList(order.getId());
            for (OrderDetail orderDetail : orderDetails) {
                int count = Integer.parseInt(orderDetail.getNumber());
                thingMapper.updateThingRepertory(orderDetail.getThingId(), -count);
                Thing thing = thingMapper.selectById(orderDetail.getThingId());
                ops.put("thingRepertory", String.valueOf(orderDetail.getThingId()), String.valueOf(thing.getRepertory()));
            }
            if (message != null) {
                Object msg = message.createMsg(order.getId(), status);
                if (msg != null) {
                    message.sendMsg(msg);
                }
            }
        }
        orderMapper.updateById(order);
    }

    @Override
    public List<Order> getUserOrderList(Long userId, String status) {
        List<Order> userOrderList = orderMapper.getUserOrderList(userId, status);
        return userOrderList.stream().peek((userOrder -> {
            List<OrderDetail> orderDetails = orderDetailService.getOrderDetailList(userOrder.getId());
            userOrder.setOrderDetails(orderDetails);
        })).collect(Collectors.toList());
    }

}
