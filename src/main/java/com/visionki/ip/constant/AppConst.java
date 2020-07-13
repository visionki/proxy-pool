package com.visionki.ip.constant;

/**
 * @Author: vision
 * @CreateDate: 2020/7/7 11:32
 * @Version: 1.0
 * @Copyright: Copyright (c) 2020
 * @Description: 常量类
 */
public class AppConst {
    /**
     * 默认遍历页面数量
     */
    public static final int PAGE_SIZE = 30;
    /**
     * 测试代理IP，HTTP链接
     */
    public static final String CHECK_HTTP_URL = "http://www.baidu.com";
    /**
     * 测试代理IP，HTTPS链接
     */
    public static final String CHECK_HTTPS_URL = "https://www.baidu.com";
    /**
     * 检查IP线程数量
     */
    public static final int CHECK_THREAD_SIZE = 10;
    /**
     * IP不可用标记时长（10分钟，单位毫秒）
     */
    public static final int INVALID_TIME = 10 * 60 * 1000;
}
