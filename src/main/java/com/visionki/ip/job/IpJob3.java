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
 * @CreateDate: 2020/7/8 17:58
 * @Version: 1.0
 * @Copyright: Copyright (c) 2020
 * @Description: 链接：http://www.89ip.cn
 */
public class IpJob3 extends Thread{


    private IpInfoService ipInfoService;

    public IpJob3(IpInfoService ipInfoService){
        this.ipInfoService = ipInfoService;
    }

    @Override
    public void run() {
        String url = "http://www.89ip.cn/index_{page}.html";
        for (int i = 1; i < AppConst.PAGE_SIZE; i++){
            try {
                HttpResponse<String> jsonResponse = Unirest.get(url.replace("{page}",i + ""))
                        .asString();
                Document document = Jsoup.parse(jsonResponse.getBody());
                Elements trList = document.getElementsByTag("tbody").get(0).getElementsByTag("tr");
                if (trList.size() == 0){
                    break;
                }
                for (Element element : trList){
                    Elements tdList = element.getElementsByTag("td");
                    IpInfo ipInfo = new IpInfo();
                    ipInfo.setIp(tdList.get(0).text());
                    ipInfo.setPort(tdList.get(1).text());
                    ipInfo.setLocation(tdList.get(2).text());
                    ipInfo.setCompany(tdList.get(3).text());
                    ipInfo.setType("HTTP");
                    ipInfo.setAnonymous("未知");
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


    public static void main(String[] args) throws UnirestException {
    }
}
