package com.visionki.ip.task;

import com.visionki.ip.constant.AppConst;
import com.visionki.ip.model.IpInfo;
import com.visionki.ip.service.IpInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: vision
 * @CreateDate: 2020/7/7 18:28
 * @Version: 1.0
 * @Copyright: Copyright (c) 2020
 * @Description: 30秒检测一次IP库
 */
@Component
@Configuration
@EnableScheduling
public class CheckIpTask {

    @Autowired
    private IpInfoService ipInfoService;

    private static List<IpInfo> ipInfoList = new ArrayList<>();
    private static int index = 0;


    public static synchronized IpInfo getIpInfo(){
        if (index == ipInfoList.size()){
            return null;
        }
        IpInfo ipInfo = ipInfoList.get(index);
        index++;
        return ipInfo;
    }

    @Scheduled(fixedRate=180 * 1000)
    private void loadIpTask() {
        // 重置数据
        List<IpInfo> ipInfoList = ipInfoService.getAllCheckIpList();
        CheckIpTask.ipInfoList = ipInfoList;
        CheckIpTask.index = 0;
        // 开启线程检查ip
        if (ipInfoList.size() > 0){
            for (int i = 0;i < AppConst.CHECK_THREAD_SIZE;i++){
                ipInfoService.checkIp();
            }
        }
    }
}