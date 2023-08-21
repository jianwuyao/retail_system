package com.jwyao.system.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收货地址
 */
@Data
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    public Long id;

    public Long userId;
    
    public String name;
    
    public String mobile;
    
    public String path;
    
    public Integer isDefault;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
