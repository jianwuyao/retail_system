package com.jwyao.system.factory;

import com.jwyao.system.entity.Order;
import com.jwyao.system.entity.OrderDetail;
import com.jwyao.system.entity.User;
import com.jwyao.system.mapper.OrderMapper;
import com.jwyao.system.service.OrderDetailService;
import com.jwyao.system.service.UserService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Email implements IMessage {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private UserService userService;

    @Override
    public Object createMsg(Long orderId, String status) {
        // MQ异步发送通知邮件
        Order orderInfo = orderMapper.selectById(orderId);
        User userDetail = userService.getUserDetail(orderInfo.getUserId());
        if (userDetail.getPushSwitch() != null && userDetail.getPushSwitch() == 1 && userDetail.getPushEmail() != null) {
            List<OrderDetail> orderDetails = orderDetailService.getOrderDetailList(orderId);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            Map<String, String> msg = new HashMap<>();
            msg.put("pushEmail", userDetail.getPushEmail());
            msg.put("userName", orderInfo.getUserName());
            msg.put("orderId", String.valueOf(orderInfo.getId()));
            msg.put("currentTime", formatter.format(LocalDateTime.now()));
            msg.put("status", status);
            if ("2".equals(status)) {
                StringBuilder detailInfo = new StringBuilder();
                for (OrderDetail orderDetail : orderDetails) {
                    detailInfo.append(orderDetail.getTitle())
                            .append("_￥").append(orderDetail.getPrice())
                            .append("_X").append(orderDetail.getNumber())
                            .append("  ");
                }
                msg.put("detailInfo", detailInfo.toString());
                msg.put("amount", orderInfo.getAmount());
                msg.put("receiverName", orderInfo.getReceiverName());
                msg.put("receiverPhone", orderInfo.getReceiverPhone());
                msg.put("receiverAddress", orderInfo.getReceiverAddress());
            }
            return msg;
        } else {
            return null;
        }
    }

    @Override
    public void sendMsg(Object msg) {
        rabbitTemplate.convertAndSend("msg.direct", "email", msg);
    }

}
