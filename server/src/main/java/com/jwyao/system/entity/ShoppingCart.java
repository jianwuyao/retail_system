package com.jwyao.system.entity;

import lombok.Data;

/**
 * 购物车
 */
@Data
public class ShoppingCart {

    private static final long serialVersionUID = 1L;

    public Long id;

    public Long userId;

    public Long thingId;

    public Long classificationId;

    public String title;

    public String price;

    public Integer count;

}
