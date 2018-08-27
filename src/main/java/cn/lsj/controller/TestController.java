package cn.lsj.controller;

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

    @GetMapping("/redis/def")
    public String getData(){
        String key = "token#" + UUID.randomUUID();
        Map<String, String> map = new HashMap<>();
        map.put("01", "hahahaha");
        map.put("02", "hehehehe");
        redisService.putTest(key,map);
        String str = redisService.getTset(key,"01");
        System.out.println("redis的值:-----"+str);
        return str;
    }

    @GetMapping("/redis/abc")
    public String getTest(){
        String str = "测试路由";
        System.out.println("redis的值:-----"+str);
        return str;
    }


}
