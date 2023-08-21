package com.jwyao.system.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户
 */
@Data
@TableName("users")
public class User implements Serializable {

    public static final int NORMAL_USER = 1;
    public static final int ADMIN_USER = 2;

    private static final long serialVersionUID = 1L;

    public Long id;
    
    public String username;
    
    public String password;

    @TableField(exist = false)
    public String rePassword;
    
    public String nickname;
    
    public String mobile;
    
    public String email;
    
    public String description;
    
    public Integer role;
    
    public Integer status;
    
    public Integer score;
    
    public String avatar;

    @TableField(exist = false)
    public MultipartFile avatarFile;
    
    public String token;
    
    public String pushEmail;
    
    public Integer pushSwitch;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
