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
public class RedisConnectionPool {

    @Value("#{redisConfig.getConnectionFactory()}")
    JedisConnectionFactory connectionFactory;

    private RedisConnectionPool(){}

    private static Jedis jedis = null;
     //获取redis链接
     public synchronized Jedis getJedis(){
         if(jedis==null){
             RedisConnection redisConnection = connectionFactory.getConnection();
             jedis = (Jedis) redisConnection.getNativeConnection();
             return jedis;
         }
        return jedis;
     }



    public static void main(String[] args) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(200);
        poolConfig.setMaxIdle(10);
        poolConfig.setMinIdle(2);
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setPort(6379);
        factory.setHostName("127.0.0.1");
        factory.setUsePool(true);
        factory.setPoolConfig(poolConfig);
        factory.afterPropertiesSet();

        RedisConnection redisConnection = factory.getConnection();
        Jedis testJedis = (Jedis) redisConnection.getNativeConnection();
            try {

                String key = "token#" + UUID.randomUUID();
                Map<String, String> map = new HashMap<>();
                map.put("01", "hahahaha");
                map.put("02", "hehehehe");
                testJedis.hmset(key, map);
                testJedis.expire(key, 3600);

                System.out.println(testJedis.hget(key, "01"));

            }catch (Exception e){
                e.printStackTrace();
            }

    }
}
