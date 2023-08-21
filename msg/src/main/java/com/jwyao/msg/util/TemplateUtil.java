package com.jwyao.msg.util;

public class TemplateUtil {

    public static String paySuccessTemplate(String userName, String orderId, String currentTime,
                                            String detailInfo, String amount, String receiverInfo) {
        String content = userName + "先生/女士：" + "\n" +
                "您好！您的【订单】#" + orderId + "【详情】" + detailInfo + "\n" +
                "【金额】" + amount + "【收货信息】" + receiverInfo + "\n" +
                "已在" + currentTime + "支付成功!" + "\n" +
                "我们将尽快为您发货！如有疑问，请联系客服！";
        return content;
    }

    public static String cancelOrderTemplate(String userName, String orderId, String currentTime) {
        String content = userName + "先生/女士：" + "\n" +
                "您好！您的【订单】#" + orderId + "已在" + currentTime + "取消成功!" + "\n" +
                "如有疑问，请联系客服！";
        return content;
    }

}
