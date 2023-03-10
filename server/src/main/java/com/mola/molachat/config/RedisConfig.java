package com.mola.molachat.config;

import com.mola.molachat.utils.FastJson2JsonRedisSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author : molamola
 * @Project: molachat
 * @Description:
 * @date : 2020-06-08 13:58
 **/
@Configuration
public class RedisConfig {

    @Autowired
    private SelfConfig selfConfig;

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        // 初始化redisTemplate
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        //开启事务
        redisTemplate.setEnableTransactionSupport(true);

        //序列化方式，采用json
        FastJson2JsonRedisSerializer serializer = new FastJson2JsonRedisSerializer(Object.class);

        //key序列化方式，采用string
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //键的序列化Sring
        redisTemplate.setValueSerializer(serializer);

        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(serializer);
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
        //设置连接池，此处使用Luttuce
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
