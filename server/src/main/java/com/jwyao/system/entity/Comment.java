package com.jwyao.system.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论
 */
@Data
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    public Long id;

    public String content;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    public Integer likeCount;

    public Long userId;

    @TableField(exist = false)
    public String username;

    public Long thingId;

    @TableField(exist = false)
    public String title;

    @TableField(exist = false)
    public String cover;

}
