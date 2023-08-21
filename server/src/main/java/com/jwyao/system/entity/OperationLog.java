package com.jwyao.system.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录与操作日志
 */
@Data
public class OperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    public Long id;
    
    public String reIp;
    
    public String reTime;
    
    public String reUa;
    
    public String reUrl;
    
    public String reMethod;
    
    public String reContent;
    
    public String accessTime;

}
