package com.jwyao.system.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单明细
 */
@Data
public class OrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    public Long id;

    public Long orderId;

    public Long thingId;

    public Long classificationId;

    public String title;

    public String price;

    public String number;

}
