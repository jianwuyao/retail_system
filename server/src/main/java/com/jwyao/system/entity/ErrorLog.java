package com.jwyao.system.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 错误日志
 */
@Data
public class ErrorLog implements Serializable {

    private static final long serialVersionUID = 1L;

    public Long id;
    
    public String ip;
    
    public String url;
    
    public String method;
    
    public String content;
    
    public String logTime;

}
