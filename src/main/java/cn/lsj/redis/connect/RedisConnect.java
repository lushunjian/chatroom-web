package cn.lsj.redis.connect;

import cn.lsj.redis.service.RedisAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

/**
 * @Auther: Lushunjian
 * @Date: 2018/8/21 23:11
 * @Description:
 */
@Component
public class RedisConnect<T> {

    private Logger logger = LoggerFactory.getLogger(RedisConnect.class);

    @Value("#{redisConfig.getConnectionFactory()}")
    private JedisConnectionFactory connectionFactory;

    private RedisConnect(){}

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

    //获取redis链接
    public synchronized T doRedis(RedisAction<T> redisAction){
        RedisConnection redisConnection = null;
         try {
             redisConnection = connectionFactory.getConnection();
             Jedis redis = (Jedis) redisConnection.getNativeConnection();
             return redisAction.doRedisAction(redis);
         }catch (Exception e){
             e.printStackTrace();
             logger.info("-----后台异常！");
             return null;
         }finally {
            if(redisConnection != null && !redisConnection.isClosed())
                redisConnection.close();
         }
    }
}
