package com.visionki.ip.job;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.visionki.ip.constant.AppConst;
import com.visionki.ip.model.IpInfo;
import com.visionki.ip.service.IpInfoService;
import com.visionki.ip.util.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @Author: vision
 * @CreateDate: 2020/7/7 11:12
 * @Version: 1.0
 * @Copyright: Copyright (c) 2020
 * @Description: 数据源：高可用全球免费代理IP库
 *                 链接：https://ip.jiangxianli.com/?page=1
 */
public class IpJob1 extends Thread{

    private IpInfoService ipInfoService;

    public IpJob1(IpInfoService ipInfoService){
        this.ipInfoService = ipInfoService;
    }

    @Override
    public void run() {
        for (int i = 1; i < AppConst.PAGE_SIZE; i++){
            try {
                HttpResponse<String> jsonResponse = Unirest.get("https://ip.jiangxianli.com/?page=" + i)
                        .asString();
                Document document = Jsoup.parse(jsonResponse.getBody());
                Elements trList = document.getElementsByClass("layui-table").get(0).getElementsByTag("tbody").get(0).getElementsByTag("tr");
                if (trList.size() == 0){
                    break;
                }
                for (Element element : trList){
                    Elements tdList = element.getElementsByTag("td");
                    IpInfo ipInfo = new IpInfo();
                    ipInfo.setIp(tdList.get(0).text());
                    ipInfo.setPort(tdList.get(1).text());
                    ipInfo.setAnonymous(tdList.get(2).text());
                    ipInfo.setType(tdList.get(3).text());
                    ipInfo.setLocation(tdList.get(4).text());
                    ipInfo.setCountry(tdList.get(5).text());
                    ipInfo.setCompany(tdList.get(6).text());
                    ipInfo.setSpeed(tdList.get(7).text());
                    ipInfo.setCreateTime(DateUtils.getCurrentDateTime(DateUtils.DATE_TIME_FORMAT));
                    ipInfoService.insertIpInfoToNoCheck(ipInfo);
                }
            } catch (UnirestException e) {
                e.printStackTrace();
            }
            // 休眠，防止过快被封
            try {
                Thread.sleep(800);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
