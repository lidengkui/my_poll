package com.poll.redis.conf;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashMap;
import java.util.Map;


@Configuration 
@EnableCaching
public class RedisConf extends CachingConfigurerSupport {

    @Bean
    public CacheManager cacheManager(RedisTemplate<?, ?> redisTemplate) {
    	
    	RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
      
    	//设置默认过期时间
    	cacheManager.setDefaultExpiration(3600);

    	//设置指定cacheName的过期时间
    	Map<String, Long> expires = new HashMap<String, Long>();
    	expires.put("second2", 2L);
    	expires.put("second5", 5L);
    	expires.put("second10", 10L);
    	expires.put("second30", 30L);
    	expires.put("minute1", 60L);
    	expires.put("minute2", 120L);
    	expires.put("minute5", 300L);
    	expires.put("minute10", 600L);
    	expires.put("minute30", 1800L);
    	expires.put("hour1", 3600L);
    	expires.put("hour2", 7200L);
    	expires.put("hour5", 18000L);
    	expires.put("hour10", 36000L);
    	expires.put("day1", 86400L);
    	expires.put("day2", 172800L);
    	expires.put("day5", 432000L);
    	expires.put("day10", 864000L);
    	expires.put("day30", 2592000L);
    	cacheManager.setExpires(expires);
       
    	return cacheManager;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
       
    	RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
    	redisTemplate.setConnectionFactory(factory);
       
    	//设置key序列化
    	redisTemplate.setKeySerializer(stringRedisSerializer());
    	//设置value序列化
    	redisTemplate.setValueSerializer(jdkSerializationRedisSerializer());
//    	redisTemplate.setValueSerializer(jackson2JsonRedisSerializer());

    	return redisTemplate;
    }
    
    @Bean
    public StringRedisSerializer stringRedisSerializer() {
    	return new StringRedisSerializer();
    }
    
    @Bean
    public JdkSerializationRedisSerializer jdkSerializationRedisSerializer() {
    	return new JdkSerializationRedisSerializer();
    }

    @Bean
    public Jackson2JsonRedisSerializer jackson2JsonRedisSerializer() {
    	return new Jackson2JsonRedisSerializer(Object.class);
    }

}
