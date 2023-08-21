package com.jwyao.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单
 */
@Data
@TableName("orders")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    public Long id;

    public String status;

    public LocalDateTime orderTime;

    public LocalDateTime payTime;

    public Long userId;

    public String userName;

    public String amount;

    public String receiverName;

    public String receiverPhone;

    public String receiverAddress;

    public String remark;

    @TableField(exist = false)
    public List<OrderDetail> orderDetails;

}
