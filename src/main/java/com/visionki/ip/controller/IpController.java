package com.visionki.ip.controller;

import com.visionki.ip.model.IpInfo;
import com.visionki.ip.service.IpInfoService;
import com.visionki.ip.util.RUtil;
import com.visionki.ip.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: vision
 * @CreateDate: 2020/7/9 16:38
 * @Version: 1.0
 * @Copyright: Copyright (c) 2020
 * @Description:
 */
@RestController
public class IpController {

    @Autowired
    private IpInfoService ipInfoService;

    @GetMapping("/getAllToString")
    public String getAllToString(String type){
        List<IpInfo> ipList = ipInfoService.getAllAvailableIpList(type);
        StringBuffer sb = new StringBuffer();
        for (IpInfo ipInfo : ipList){
            sb.append(ipInfo.getIp()).append(":").append(ipInfo.getPort()).append("\n\r");
        }
        return sb.toString();
    }

    @GetMapping("/getToJson")
    public R getToJson(String type){
        List<IpInfo> ipList = ipInfoService.getAllAvailableIpList(type);
        return RUtil.success(ipList);
    }


}
