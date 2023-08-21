package com.jwyao.system.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 广告
 */
@Data
public class Advertisement implements Serializable {

    private static final long serialVersionUID = 1L;

    public Long id;
    
    public String image;

    @TableField(exist = false)
    public MultipartFile imageFile;
    
    public String link;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
