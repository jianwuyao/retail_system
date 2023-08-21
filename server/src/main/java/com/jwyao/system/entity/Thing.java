package com.jwyao.system.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品
 */
@Data
public class Thing implements Serializable {

    private static final long serialVersionUID = 1L;

    public Long id;
    
    public String title;
    
    public String cover;
    
    public String description;
    
    public String price;
    
    public Integer status;

    public Integer repertory;

    public Integer browseCount;

    public Long classificationId;

    @TableField(exist = false)
    public Integer recommendCount;

    @TableField(exist = false)
    public Integer wishCount;

    @TableField(exist = false)
    public Integer collectCount;

    @TableField(exist = false)
    public String classification;

    @TableField(exist = false)
    public List<Long> tags;

    @TableField(exist = false)
    public MultipartFile imageFile;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
