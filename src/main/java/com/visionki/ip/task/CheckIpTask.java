package com.visionki.ip.task;

import com.visionki.ip.constant.AppConst;
import com.visionki.ip.model.IpInfo;
import com.visionki.ip.service.IpInfoService;
import lombok.extern.slf4j.Slf4j;
import org.omg.PortableServer.LIFESPAN_POLICY_ID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

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
@Slf4j
public class CheckIpTask {

    @Autowired
    private IpInfoService ipInfoService;

    private static List<IpInfo> ipInfoList = new ArrayList<>();
    private static int index = 0;
    private static List<Future<String>> futures = new ArrayList<>(AppConst.CHECK_THREAD_SIZE);

    /**
     * 获取IP
     * @return
     */
    public static synchronized IpInfo getIpInfo(){
        if (index == ipInfoList.size()){
            return null;
        }
        IpInfo ipInfo = ipInfoList.get(index);
        index++;
        return ipInfo;
    }

    @Scheduled(fixedRate=60 * 1000)
    private void loadIpTask() {
        // 判断上个任务执行完了没
        if (futures.size() > 0){
            for (Future<String> future : futures ){
                if (!future.isDone()){
                    log.warn("上一次的还没全部执行完，跳过这次");
                    return;
                }
            }
        }
        // 重置数据
        List<IpInfo> ipInfoList = ipInfoService.getAllCheckIpList();
        CheckIpTask.ipInfoList = ipInfoList;
        CheckIpTask.index = 0;
        CheckIpTask.futures = new ArrayList<>(AppConst.CHECK_THREAD_SIZE);
        // 开启线程检查ip
        if (ipInfoList.size() > 0){
            for (int i = 0;i < AppConst.CHECK_THREAD_SIZE;i++){
                futures.add(ipInfoService.checkIp());
            }
        }
    }
}