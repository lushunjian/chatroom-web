package cn.lsj.controller;

import cn.lsj.domain.User;
import cn.lsj.redis.service.RedisHandler;
import cn.lsj.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    RedisService redisService;

    @Autowired
    RedisHandler redisHandler;

    @GetMapping("/redis")
    public String getData(){
        String key = "token#" + UUID.randomUUID();
        Map<String, String> map = new HashMap<>();
        map.put("01", "hah");
        map.put("02", "aaa");
        redisService.putTest(key,map);
        String str = redisService.getTest(key,"01");

        redisService.put(key,"123");
        String strs = redisService.get(key);
        System.out.println("redis的值:-----"+strs);
        return str;
    }

    /**
     * 测试对象存储
     * */
    @GetMapping("/redis/putObject")
    public String putObject(){
        User user = new User();
        user.setUserName("redis对象存储");
        user.setUserDescribe("随便写点东西");
        redisService.jedisPutObject("user",user);
        return "success";
    }

    @GetMapping("/redis/getObject")
    public String getObject(){
        User user = (User) redisService.jedisGetObject("user");
        return user.getUserName()+"---"+user.getUserDescribe();
    }

    @GetMapping("/redis/object")
    public String getObjectTest(){
        User user = new User();
        user.setUserName("redis对象存储");
        user.setUserDescribe("随便写点东西");
        redisHandler.putObject("user",user);
        User u = (User) redisHandler.getObject("user");
        return u.getUserName()+"---"+u.getUserDescribe();
    }

}
