package com.jwyao.system.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品-标签关联
 */
@Data
public class ThingTag implements Serializable {

    private static final long serialVersionUID = 1L;

    public Long id;
    
    public Long thingId;
    
    public Long tagId;

}
