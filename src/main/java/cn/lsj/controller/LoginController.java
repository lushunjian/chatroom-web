package cn.lsj.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Auther: Lushunjian
 * @Date: 2018/8/23 07:49
 * @Description:
 */
//Controller注解返回到页面
@Controller
public class LoginController {

    @GetMapping("/login")
    public String getLoginHtml(){
        return "/test";
    }

}
