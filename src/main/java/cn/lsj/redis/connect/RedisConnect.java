package cn.lsj.redis.connect;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Auther: Lushunjian
 * @Date: 2018/8/21 23:11
 * @Description:
 */
@Component
public class RedisConnect {

    @Value("#{redisConfig.getConnectionFactory()}")
    private JedisConnectionFactory connectionFactory;

    private RedisConnect(){}

    private static Jedis jedis = null;
     //获取redis链接
     public synchronized Jedis getJedis(){
         if(jedis==null){
             org.springframework.data.redis.connection.RedisConnection redisConnection = connectionFactory.getConnection();
             jedis = (Jedis) redisConnection.getNativeConnection();
             return jedis;
         }
        return jedis;
     }

}
