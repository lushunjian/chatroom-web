package cn.lsj.controller;

import cn.lsj.domain.Friend;
import cn.lsj.domain.User;
import cn.lsj.redis.service.RedisHandler;
import cn.lsj.service.FriendService;
import cn.lsj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Auther: Lushunjian
 * @Date: 2018/8/23 07:49
 * @Description:
 */
//Controller注解返回到页面
@Controller
public class LoginController {

    private static final String token = "token#";

    @Autowired
    private UserService userService;

    @Autowired
    private FriendService friendService;

    @Autowired
    private RedisHandler redisHandler;

    @GetMapping("/login")
    public String getLoginHtml(HttpServletRequest request){
        String status = request.getParameter("status");
        if(status == null)
            return "/login";
        else {
            request.setAttribute("cssStyle",request.getParameter("cssStyle"));
            request.setAttribute("status",request.getParameter("status"));
            request.setAttribute("message",request.getParameter("message"));
            return "/login";
        }
    }

    @RequestMapping("/home")
    public String getHome(HttpServletRequest request){
        //从请求的cookie中获取用户账号
        String userAccount = null;
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies){
            if(cookie.getName().equals("userAccount")){
                userAccount = cookie.getValue();
                break;
            }
        }
        //根据账号查询好友列表
        List<Friend> friendList = friendService.getFriendByAccount(userAccount);
        User user = (User) redisHandler.getObject(request.getSession().getId());
        request.setAttribute("user",user);
        request.setAttribute("friendList",friendList);
        return "/home/index";
    }

    @PostMapping("/login")
    public String userLogin(String userAccount, String userPassword, HttpServletRequest request, HttpServletResponse response, RedirectAttributes attr){
        User user = userService.getUserInfo(userAccount,userPassword);
        if(user != null) {
            //保存用户信息 保存在redis中，表示已登录
            //保存sessionId，用于判断用户异地登录
            String sessionId = request.getSession().getId();
            try {
                redisHandler.putObject(userAccount, sessionId);
                redisHandler.putObject(sessionId, user);
                //设置过期时间为 30分钟
                redisHandler.expire(userAccount, 1800);
                redisHandler.expire(sessionId, 1800);

                Cookie cookie = new Cookie("userAccount", userAccount);
                //设置cookie过期时间
                cookie.setMaxAge(24 * 60 * 60);
                response.addCookie(cookie);
                return "redirect:/home";
            }catch (JedisConnectionException | RedisConnectionFailureException e  ){
                attr.addAttribute("cssStyle","red message");
                attr.addAttribute("status","true");
                attr.addAttribute("message","服务器异常，redis连接失败!");
                return "redirect:/login";
            }
        } else {
            attr.addAttribute("cssStyle","red message");
            attr.addAttribute("status","true");
            attr.addAttribute("message","您的账号或密码错误，登录失败!");
            return "redirect:/login";
        }
    }

    @PostMapping("/register")
    public String userRegister(String userAccount, String userPassword, String passwordConfirm, String userName, String userDescribe, RedirectAttributes attr){
        attr.addAttribute("cssStyle","red message");
        if(!userPassword.equals(passwordConfirm)){
            attr.addAttribute("status","true");
            attr.addAttribute("message","两次密码不一致，注册失败 ！");
            return "redirect:/login";
        }
        try {
            if(userService.addUser(new User(userName,userAccount,userPassword,userDescribe))){
                attr.addAttribute("cssStyle","green message");
                attr.addAttribute("status","true");
                attr.addAttribute("message","注册成功!现在你可以使用账号登录了");
            }else {
                attr.addAttribute("status","true");
                attr.addAttribute("message","注册失败!请您尝试注册");
            }
        } catch (Exception e){
            attr.addAttribute("status","true");
            attr.addAttribute("message","注册失败!服务器发生异常");
            e.printStackTrace();
        }
        return "redirect:/login";
    }
}
