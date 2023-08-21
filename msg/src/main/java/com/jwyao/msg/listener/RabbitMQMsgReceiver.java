package com.jwyao.msg.listener;

import com.jwyao.msg.util.EmailUtil;
import com.jwyao.msg.util.TemplateUtil;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 消费者异步消费消息，发送邮件
 */
@Component
public class RabbitMQMsgReceiver {

    @Autowired
    private EmailUtil emailUtil;

    private static final String TITLE = "【小型门店零售系统】订单通知";

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue"),
            exchange = @Exchange(name = "msg.direct", type = ExchangeTypes.DIRECT),
            key = {"email"}))
    public void receive(Map<String, String> msg) {
        String content = "";
        String pushEmail = msg.get("pushEmail");
        String userName = msg.get("userName");
        String orderId = msg.get("orderId");
        String currentTime = msg.get("currentTime");
        String status = msg.get("status");
        if ("2".equals(status)) {
            String detailInfo = msg.get("detailInfo");
            String amount = msg.get("amount");
            String receiverInfo = msg.get("receiverName") + " " + msg.get("receiverPhone") + " " + msg.get("receiverAddress");
            content = TemplateUtil.paySuccessTemplate(userName, orderId, currentTime, detailInfo, amount, receiverInfo);
        } else {
            content = TemplateUtil.cancelOrderTemplate(userName, orderId, currentTime);
        }
        emailUtil.sendMessage(pushEmail, TITLE, content);
    }

}
