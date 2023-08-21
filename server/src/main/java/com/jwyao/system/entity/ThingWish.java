package com.jwyao.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品-用户心愿单关联
 */
@Data
public class ThingWish implements Serializable {

    private static final long serialVersionUID = 1L;

    public Long id;
    
    public Long thingId;
    
    public Long userId;

    @TableField(exist = false)
    public String title;

    @TableField(exist = false)
    public String cover;

}
