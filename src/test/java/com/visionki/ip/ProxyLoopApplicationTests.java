package com.visionki.ip;

import com.visionki.ip.service.IpInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootTest
class ProxyLoopApplicationTests {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private IpInfoService ipInfoService;


    @Test
    void contextLoads()  {
    }

}
