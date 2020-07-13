package com.visionki.ip.service;

import com.visionki.ip.constant.AppConst;
import com.visionki.ip.dao.AvailableIpPoolDao;
import com.visionki.ip.dao.CheckIpPoolDao;
import com.visionki.ip.dao.InvalidIpPoolDao;
import com.visionki.ip.model.InvalidIpInfo;
import com.visionki.ip.model.IpInfo;
import com.visionki.ip.task.CheckIpTask;
import com.visionki.ip.util.DateUtils;
import com.visionki.ip.util.ProxyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @Author: vision
 * @CreateDate: 2020/7/7 14:24
 * @Version: 1.0
 * @Copyright: Copyright (c) 2020
 * @Description:
 */
@Service
@Slf4j
public class IpInfoService {

    @Autowired
    private CheckIpPoolDao checkIpPoolDao;
    @Autowired
    private AvailableIpPoolDao availableIpPoolDao;
    @Autowired
    private InvalidIpPoolDao invalidIpPoolDao;



    /**
     * 将信息加入到待检查IP库
     * @param ipInfo
     */
    public void insertIpInfoToNoCheck(IpInfo ipInfo) {
        // 检查是否存在，不存在则录入
        IpInfo temp = checkIpPoolDao.getByIp(ipInfo.getIp());
        if (temp == null){
            ipInfo.setCreateTime(DateUtils.getCurrentDateTime(DateUtils.DATE_TIME_FORMAT));
            checkIpPoolDao.insert(ipInfo);
        }
    }

    /**
     * 获取全部待检测IP
     * @return
     */
    public List<IpInfo> getAllCheckIpList() {
        return checkIpPoolDao.getAllCheckIpList();
    }

    /**
     * 获取全部可用IP
     * @return
     * @param type
     */
    public List<IpInfo> getAllAvailableIpList(String type) {
        return availableIpPoolDao.getAllCheckIpList(type);
    }

    /**
     * 异步检测IP
     * @return
     */
    @Async("taskExecutor")
    public Future<String> checkIp() {
        while (true){
            IpInfo ipInfo = CheckIpTask.getIpInfo();
            if (ipInfo == null){
                log.info("线程{}检查IP已完成",Thread.currentThread().getName());
                return new AsyncResult<>(Thread.currentThread().getName());
            }
            // 检查IP
            try {
                // 利用代理访问测试接口
                boolean available;
                long start = System.currentTimeMillis();
                if ("HTTP".equals(ipInfo.getType())){
                    available = ProxyUtil.validateHttp(ipInfo.getIp(), Integer.parseInt(ipInfo.getPort()));
                }else {
                    available = ProxyUtil.validateHttps(ipInfo.getIp(), Integer.parseInt(ipInfo.getPort()));
                }
                long end = System.currentTimeMillis();
                long time = end - start;
                ipInfo.setSpeed(time + "ms");
                ipInfo.setLastCheckTime(DateUtils.getCurrentDateTime(DateUtils.DATE_TIME_FORMAT));
                if (available){
                    log.info("线程{}检查IP：{}，port：{}，状态：{}，响应时间：{}ms",Thread.currentThread().getName(),ipInfo.getIp(),ipInfo.getPort(),available,time);
                    signAvailable(ipInfo);
                }else {
                    log.error("线程{}检查IP：{}，port：{}，状态：{}，响应时间：{}ms",Thread.currentThread().getName(),ipInfo.getIp(),ipInfo.getPort(),available,time);
                    signInvalid(ipInfo);
                }
            }catch (Exception e){
                signInvalid(ipInfo);
                e.printStackTrace();
            }
        }
    }

    /**
     * 标记为可用IP
     * @param ipInfo
     */
    private void signAvailable(IpInfo ipInfo){
        // 更新可用IP库
        availableIpPoolDao.upsert(ipInfo);
        // 从不可用库中删除
        invalidIpPoolDao.removeByIp(ipInfo.getIp());
    }

    /**
     * 标记为不可用IP
     * @param ipInfo
     */
    private void signInvalid(IpInfo ipInfo){
        IpInfo availableIp = availableIpPoolDao.getByIp(ipInfo.getIp());
        if (availableIp != null){
            availableIpPoolDao.removeByIp(ipInfo.getIp());
        }else {
            checkIpPoolDao.removeByIp(ipInfo.getIp());
            InvalidIpInfo invalidIpInfo = new InvalidIpInfo();
            invalidIpInfo.setIp(ipInfo.getIp());
            invalidIpInfo.setPort(ipInfo.getPort());
            invalidIpInfo.setCheckTime(DateUtils.getCurrentDateTime(DateUtils.DATE_TIME_FORMAT));
            invalidIpPoolDao.upsertByIp(invalidIpInfo);
        }
    }

    /**
     * 检测是否处于不可用状态（若IP不可用，10分钟内标记为不可用状态）
     * @param ip
     * @param port
     * @return true - 不可用，false - 可用
     */
    public boolean checkInInvalid(String ip,String port){
        InvalidIpInfo invalidIpInfo = invalidIpPoolDao.getIpInfo(ip,port);
        if (invalidIpInfo == null){
            return false;
        }
        Date date = DateUtils.parseDate(invalidIpInfo.getCheckTime(), DateUtils.DATE_TIME_FORMAT);
        return System.currentTimeMillis() - date.getTime() <= AppConst.INVALID_TIME;
    }



}