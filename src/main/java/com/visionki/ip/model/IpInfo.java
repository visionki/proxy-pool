package com.visionki.ip.model;

import lombok.Data;

/**
 * @Author: vision
 * @CreateDate: 2020/7/7 11:35
 * @Version: 1.0
 * @Copyright: Copyright (c) 2020
 * @Description: ip类
 */
@Data
public class IpInfo {
    private String id;
    private String ip;
    private String port;
    /**
     * 匿名类型
     */
    private String anonymous;
    /**
     * http类型
     */
    private String type;
    private String location;
    private String country;
    private String company;
    private String speed;
    private String createTime;
    private String lastCheckTime;


}
