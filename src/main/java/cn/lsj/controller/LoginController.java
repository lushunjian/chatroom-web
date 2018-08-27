package cn.lsj.controller;

import cn.lsj.domain.Friend;
import cn.lsj.domain.User;
import cn.lsj.service.FriendService;
import cn.lsj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Auther: Lushunjian
 * @Date: 2018/8/23 07:49
 * @Description:
 */
//Controller注解返回到页面
@Controller
public class LoginController {

    @Autowired
    UserService userService;

    @Autowired
    FriendService friendService;

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
        String userAccount = request.getParameter("userAccount");
        List<Friend> friendList = friendService.getFriendByAccount(userAccount);
        User user = (User) request.getSession().getAttribute("user");
        request.setAttribute("user",user);
        request.setAttribute("friendList",friendList);
        return "/home/index";
    }

    @PostMapping("/login")
    public String userLogin(String userAccount, String userPassword, HttpServletRequest request,RedirectAttributes attr){
        User user = userService.getUserInfo(userAccount,userPassword);
        if(user != null) {
            //将用户信息保存在 session中，表示已登录
            request.getSession().setAttribute("user", user);
            return "redirect:/home?userAccount="+userAccount;
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
