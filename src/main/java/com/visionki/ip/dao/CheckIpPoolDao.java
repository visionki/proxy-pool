package com.visionki.ip.dao;

import com.visionki.ip.model.IpInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @Author: vision
 * @CreateDate: 2020/7/7 14:45
 * @Version: 1.0
 * @Copyright: Copyright (c) 2020
 * @Description: 待确认Ip池
 */
@Repository
public class CheckIpPoolDao {

    @Autowired
    private MongoTemplate mongoTemplate;
    /**
     * 待检查IP池表名
     */
    private final String tableName = "check_ip_pool";

    /**
     * 根据Ip获取代理信息
     * @param ip
     * @return
     */
    public IpInfo getByIp(String ip) {
        Query query = new Query(Criteria.where("ip").is(ip));
        return mongoTemplate.findOne(query, IpInfo.class, tableName);
    }

    /**
     * 新增代理IP信息到待检查池
     * @param ipInfo
     * @return
     */
    public IpInfo insert(IpInfo ipInfo) {
        return mongoTemplate.insert(ipInfo, tableName);
    }

    public List<IpInfo> getAllCheckIpList() {
        Query query = new Query();
        return mongoTemplate.find(query, IpInfo.class, tableName);
    }


    public void removeByIp(String ip) {
        Query query = new Query(Criteria.where("ip").is(ip));
        mongoTemplate.remove(query,tableName);
    }
}
