package com.visionki.ip.constant;

/**
 * @Author: vision
 * @CreateDate: 2020/7/7 11:08
 * @Version: 1.0
 * @Copyright: Copyright (c) 2020
 * @Description: 返回前端错误信息枚举类
 */
public enum REnum {
    /* 1~10000 系统级别错误 */
    ERROR(500,"出现错误，请联系管理员"),
    ;

    private Integer code;

    private String message;

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    REnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
