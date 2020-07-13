package com.visionki.ip.model;

import lombok.Data;

/**
 * @Author: vision
 * @CreateDate: 2020/7/13 10:23
 * @Version: 1.0
 * @Copyright: Copyright (c) 2020
 * @Description:
 */
@Data
public class InvalidIpInfo {
    private String id;
    private String ip;
    private String port;
    private String checkTime;
}