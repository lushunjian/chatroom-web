package cn.lsj.interceptor;

import cn.lsj.domain.User;
import cn.lsj.redis.service.RedisHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {

    private static final String token = "token#";

    @Autowired
    RedisHandler redisHandler;

/**
     * 在请求处理之前进行调用，在进入Controller方法之前先进入此方法
     * 返回true才会继续向下执行，返回false取消当前请求
     * 登录拦截，权限资源控制工作
     * */

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String userAccount = httpServletRequest.getParameter("userAccount");
        //从redis中拿取用户信息
        boolean flag =  redisHandler.exists(token+userAccount);
        //User user = (User)redisHandler.getObject(token+userAccount);
        //存在就放行，否则回到登录界面
        if(flag)
            return true;
        httpServletResponse.sendRedirect("/login");
        return false;

    }

/**
     * 请求处理之后进行调用，但是在视图被渲染之前进入此方法,ModelAndView
     * 日志处理工作
     * */

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

/**
     * 在整个请求结束之后被调用，就是在DispatcherServlet 渲染了对应的视图之后执行
     * 主要是用于进行资源清理工作
     * */

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
