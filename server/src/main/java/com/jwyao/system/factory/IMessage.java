package com.jwyao.system.factory;

public interface IMessage {

    Object createMsg(Long orderId, String status);

    void sendMsg(Object msg);

}
