package com.visionki.ip.task;

import com.visionki.ip.job.IpJob1;
import com.visionki.ip.job.IpJob2;
import com.visionki.ip.service.IpInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author: vision
 * @CreateDate: 2020/7/7 18:00
 * @Version: 1.0
 * @Copyright: Copyright (c) 2020
 * @Description: 每分钟执行一次IP爬取
 */
@Component
@Configuration
@EnableScheduling
public class LoadIpTask {

    @Autowired
    private IpInfoService ipInfoService;

    /**
     * 每三分钟抓取一次数据源
     */
    @Scheduled(fixedRate=300 * 1000)
    private void loadIpTask() {
        IpJob1 job1 = new IpJob1(ipInfoService);
        job1.start();
        IpJob2 job2 = new IpJob2(ipInfoService);
        job2.start();
        // 可用IP较少干脆关了
//        IpJob3 job3 = new IpJob3(ipInfoService);
//        job3.start();
    }
}
