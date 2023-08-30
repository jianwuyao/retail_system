package com.jwyao.system.factory;

public class MessageFactory {

    public static IMessage getInstance(String className) {
        if ("email".equals(className)) {
            return new Email() ;
        } else if ("ding".equals(className)) {
            return new Ding() ;
        } else if ("instation".equals(className)) {
            return null ;
        } else {
            return null;
        }
    }

}
