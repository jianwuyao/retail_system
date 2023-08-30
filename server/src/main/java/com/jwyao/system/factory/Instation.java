package com.jwyao.system.factory;

import org.springframework.stereotype.Service;

@Service
public class Instation implements IMessage {

    @Override
    public Object createMsg(Long orderId, String status) {
        System.out.println("创建站内消息...");
        return null;
    }

    @Override
    public void sendMsg(Object msg) {
        System.out.println("发送站内消息...");
    }

}