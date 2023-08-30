package com.jwyao.system.factory;

import org.springframework.stereotype.Service;

@Service
public class Ding implements IMessage {

    @Override
    public Object createMsg(Long orderId, String status) {
        System.out.println("创建钉钉消息...");
        return null;
    }

    @Override
    public void sendMsg(Object msg) {
        System.out.println("发送钉钉消息...");
    }

}
