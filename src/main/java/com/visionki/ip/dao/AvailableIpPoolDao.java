package com.visionki.ip.dao;

import com.alibaba.fastjson.JSON;
import com.visionki.ip.model.IpInfo;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @Author: vision
 * @CreateDate: 2020/7/7 14:45
 * @Version: 1.0
 * @Copyright: Copyright (c) 2020
 * @Description: 可用IP池
 */
@Repository
public class AvailableIpPoolDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    private final String tableName = "available_ip_pool";

    public IpInfo getByIp(String ip) {
        Query query = new Query(Criteria.where("ip").is(ip));
        return mongoTemplate.findOne(query, IpInfo.class, tableName);
    }

    public void upsert(IpInfo ipInfo) {
        Query query = new Query();
        query.addCriteria(Criteria.where("ip").is(ipInfo.getIp()));
        Document document = Document.parse(JSON.toJSONString(ipInfo));
        Update update = new Update();
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            if (!"id".equals(entry.getKey())){
                update.set(entry.getKey(), entry.getValue());
            }
        }
        mongoTemplate.upsert(query,update,tableName);
    }

    public void removeByIp(String ip) {
        Query query = new Query(Criteria.where("ip").is(ip));
        mongoTemplate.remove(query,tableName);
    }
}
