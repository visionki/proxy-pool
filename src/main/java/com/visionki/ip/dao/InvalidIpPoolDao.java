package com.visionki.ip.dao;

import com.alibaba.fastjson.JSON;
import com.visionki.ip.model.InvalidIpInfo;
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
 * @CreateDate: 2020/7/13 10:08
 * @Version: 1.0
 * @Copyright: Copyright (c) 2020
 * @Description: 无效IP池，不可用IP会进入这里
 */
@Repository
public class InvalidIpPoolDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    private final String tableName = "invalid_ip_pool";

    /**
     * 根据IP端口获取IP信息
     * @param ip
     * @param port
     * @return
     */
    public InvalidIpInfo getIpInfo(String ip, String port) {
        Query query = new Query(
                Criteria.where("ip").is(ip)
                .and("port").is(port)
        );
        return mongoTemplate.findOne(query, InvalidIpInfo.class, tableName);
    }

    /**
     * 更新不可用IP
     * @param invalidIpInfo
     */
    public void upsertByIp(InvalidIpInfo invalidIpInfo) {
        Query query = new Query();
        query.addCriteria(Criteria.where("ip").is(invalidIpInfo.getIp()));
        Document document = Document.parse(JSON.toJSONString(invalidIpInfo));
        Update update = new Update();
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            if (!"id".equals(entry.getKey())){
                update.set(entry.getKey(), entry.getValue());
            }
        }
        mongoTemplate.upsert(query,update,tableName);
    }

    /**
     * 从不可用库中删除
     * @param ip
     */
    public void removeByIp(String ip) {
        Query query = new Query(Criteria.where("ip").is(ip));
        mongoTemplate.remove(query,tableName);
    }
}
