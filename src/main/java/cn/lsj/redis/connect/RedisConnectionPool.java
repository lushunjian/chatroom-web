package cn.lsj.redis.connect;

import cn.lsj.redis.config.RedisConfig;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Resource;

/**
 * @Auther: Lushunjian
 * @Date: 2018/8/21 23:11
 * @Description:
 */
public class RedisConnectionPool {

    @Resource
    private static RedisConfig redisConfig;

    private RedisConnectionPool(){}

    private static JedisPool jedisPool = null;
     //获取链接
     public static synchronized Jedis getJedis(){
         if(jedisPool==null){
             //获得连接池配置
             JedisPoolConfig jedisPoolConfig = redisConfig.getRedisConfig();

             //获取redis配置
             JedisConnectionFactory factory = redisConfig.getConnectionFactory();

             //表示连接池在创建链接的时候会先测试一下链接是否可用，这样可以保证连接池中的链接都可用的。
             jedisPoolConfig.setTestOnBorrow(true);
             jedisPool = new JedisPool(jedisPoolConfig, factory.getHostName(), factory.getPort(), factory.getTimeout(), factory.getPassword());
          }
        return jedisPool.getResource();
     }

    /**
     * 释放jedis资源
     * @param jedis
     */
    public static void returnResource(final Jedis jedis) {
        if (jedis != null) {
            jedisPool.returnResource(jedis);
        }
    }
}
