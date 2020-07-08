package com.visionki.ip.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.visionki.ip.constant.AppConst;
import com.visionki.ip.dao.AvailableIpPoolDao;
import com.visionki.ip.dao.CheckIpPoolDao;
import com.visionki.ip.model.IpInfo;
import com.visionki.ip.task.CheckIpTask;
import com.visionki.ip.util.DateUtils;
import com.visionki.ip.util.HttpsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import sun.net.www.protocol.https.Handler;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.List;

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

    private static final String VALIDATE_URL = "http://www.baidu.com/";
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

    @Async("taskExecutor")
    public void checkIp() {
        while (true){
            IpInfo ipInfo = CheckIpTask.getIpInfo();
            if (ipInfo == null){
                log.info("线程{}检查IP已完成",Thread.currentThread().getName());
                return;
            }
            // 检查IP
            try {
                log.info("线程{}检查IP",Thread.currentThread().getName());
                // 利用代理访问测试接口
                boolean available;
                if ("HTTP".equals(ipInfo.getType())){
                    available = validateHttp(ipInfo.getIp(), Integer.parseInt(ipInfo.getPort()));
                }else {
                    available = validateHttps(ipInfo.getIp(), Integer.parseInt(ipInfo.getPort()));
                }
                if (available){
                    // 可用，更新到可用库
                    ipInfo.setLastCheckTime(DateUtils.getCurrentDateTime(DateUtils.DATE_TIME_FORMAT));
                    availableIpPoolDao.upsert(ipInfo);
                }else {
                    // 不可用，从可用库中删除
                    IpInfo availableIp = availableIpPoolDao.getByIp(ipInfo.getIp());
                    if (availableIp != null){
                        availableIpPoolDao.removeByIp(ipInfo.getIp());
                    }else {
                        checkIpPoolDao.removeByIp(ipInfo.getIp());
                    }
                }
            }catch (Exception e){
                // 不可用，从可用库中删除
                IpInfo availableIp = availableIpPoolDao.getByIp(ipInfo.getIp());
                if (availableIp != null){
                    availableIpPoolDao.removeByIp(ipInfo.getIp());
                }else {
                    checkIpPoolDao.removeByIp(ipInfo.getIp());
                }
                e.printStackTrace();
            }
        }
    }


    private static boolean validateHttp(String ip, int port) {
        boolean available = false;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(VALIDATE_URL);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
            connection = (HttpURLConnection) url.openConnection(proxy);
            connection.setRequestProperty("accept", "");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
            connection.setConnectTimeout(5 * 1000);
            connection.setReadTimeout(5 * 1000);
            connection.setInstanceFollowRedirects(false);
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String s = null;
            StringBuilder sb = new StringBuilder();
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            if (sb.toString().contains("baidu.com") && connection.getResponseCode() == 200) {
                available = true;
            }
            log.info("validateHttp ==> ip:{} port:{} info:{}", ip, port, connection.getResponseMessage());
        } catch (Exception e) {
            //e.printStackTrace();
            log.error("validateHttp ==> ip:{} port:{} info:{}", ip, port, "ERROR");
            available = false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return available;
    }

    private boolean validateHttps(String ip, int port) {
        boolean available = false;
        HttpsURLConnection httpsURLConnection = null;
        try {
            URL url = new URL(null, VALIDATE_URL, new Handler());
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
            httpsURLConnection = (HttpsURLConnection) url.openConnection(proxy);
            httpsURLConnection.setSSLSocketFactory(HttpsUtils.getSslSocketFactory());
            httpsURLConnection.setHostnameVerifier(HttpsUtils.getTrustAnyHostnameVerifier());
            httpsURLConnection.setRequestProperty("accept", "");
            httpsURLConnection.setRequestProperty("connection", "Keep-Alive");
            httpsURLConnection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
            httpsURLConnection.setConnectTimeout(10 * 1000);
            httpsURLConnection.setReadTimeout(10 * 1000);
            httpsURLConnection.setInstanceFollowRedirects(false);
            BufferedReader br = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
            String s = null;
            StringBuilder sb = new StringBuilder();
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            if (sb.toString().contains("baidu.com") && httpsURLConnection.getResponseCode() == 200) {
                available = true;
            }
            log.info("validateHttps ==> ip:{} port:{} info:{}", ip, port, httpsURLConnection.getResponseMessage());
        } catch (Exception e) {
            //e.printStackTrace();
            available = false;
        } finally {
            if (httpsURLConnection != null) {
                httpsURLConnection.disconnect();
            }
        }
        return available;
    }

    public static void main(String[] args) {
        boolean b = validateHttp("185.25.206.192", 8080);

    }
}
