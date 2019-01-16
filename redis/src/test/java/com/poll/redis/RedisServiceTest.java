package com.poll.redis;

import com.poll.common.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisServiceTest {

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void genToken() {
        try {
            String token_123 = redisService.genToken("token_123", 30);
            System.out.println(token_123);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void genToken1() {
        try {
            String token_123 = redisService.genToken(Constants.REDIS_KEY_PREFIX_TOKEN, "123", 10, 30);
            System.out.println(token_123);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkToken() {
        try {
            redisService.checkToken("token_123", 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void checkToken1() {
        String token = "x8GHiLCvdm";
        try {
            redisService.checkToken(Constants.REDIS_KEY_PREFIX_TOKEN, "123", token, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkTokenOnce() {
        String token = "token_123";
        try {
            redisService.checkTokenOnce(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkTokenTimeliness() {
        for (int i = 0; i < 10; i ++ ) {

            String token = "token_123";
            try {
                redisService.checkTokenTimeliness(token);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}