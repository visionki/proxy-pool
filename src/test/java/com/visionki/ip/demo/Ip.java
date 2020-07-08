package com.visionki.ip.demo;

import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @Author: vision
 * @CreateDate: 2020/7/7 11:15
 * @Version: 1.0
 * @Copyright: Copyright (c) 2020
 * @Description:
 */
public class Ip {


    public static void main(String[] args) {
        // header
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        // payload
        String body = "{\"user_id\":13644,\"mobile\":\"15768360130\"}";
        String payload = "{\"sha1\":\"" + DigestUtils.sha1Hex(body) + "\"}";



        String signature = "";
        try {
            signature = HMACSHA256(
                    Base64.getEncoder().encodeToString(header.getBytes(StandardCharsets.UTF_8))
                            + Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8))
                    , "cGILNjb8Qutaxe4UoHnP");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(
                Base64.getEncoder().encodeToString(header.getBytes(StandardCharsets.UTF_8))
                + "."
                + Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8))
                + "."
                + signature);


    }

    public static String HMACSHA256(String data, String key) throws Exception {

        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");

        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");

        sha256_HMAC.init(secret_key);

        byte[] array = sha256_HMAC.doFinal(data.getBytes("UTF-8"));

        StringBuilder sb = new StringBuilder();

        for (byte item : array) {

            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));

        }

        return sb.toString();

    }

}
