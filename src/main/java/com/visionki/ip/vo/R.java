package com.visionki.ip.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @Author: vision
 * @CreateDate: 2020/7/7 11:08
 * @Version: 1.0
 * @Copyright: Copyright (c) 2020
 * @Description: json返回包装类
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class R<T> {
    /**
     * 错误码
     */
    private Integer code;

    /**
     * 返回说明
     */
    private String msg;

    /**
     * 具体数据
     */
    private T data;
}
