package com.jwyao.system.permission;

public enum AccessLevel {

    LOGIN(1, "all"),    // 普通用户
    ADMIN(2, "admin");  // 管理员

    int code;
    String msg;

    AccessLevel(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
