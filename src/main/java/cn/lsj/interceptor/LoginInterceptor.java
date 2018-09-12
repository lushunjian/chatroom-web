package cn.lsj.interceptor;

import cn.lsj.redis.service.RedisHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.ConnectException;

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
        String userAccount = null;
        //从cookie中取值
        Cookie[] cookies = httpServletRequest.getCookies();
        // 没有获取到Cookie，对象调回登录界面
        if(cookies == null) {
            httpServletResponse.sendRedirect("/login");
            return false;
        }
        for(Cookie cookie : cookies){
            if(cookie.getName().equals("userAccount")){
                userAccount = cookie.getValue();
                break;
            }
        }
        //当前请求的sessionId
        String sessionIdNow = httpServletRequest.getSession().getId();
        //redis中的sessionId
        try {
            if (userAccount != null) {
                Object sessionObj = redisHandler.getObject(userAccount);
                //如果redis中没有查到sessionId，说明用户还没有登录，返回到登录界面
                if (sessionObj == null) {
                    httpServletResponse.sendRedirect("/login");
                    return false;
                } else {
                    String sessionId = (String) sessionObj;
                    //如果redis中查到sessionId与当前请求的sessionId不一致，说明用户是异地登录
                    if (!sessionId.equals(sessionIdNow)) {
                        httpServletResponse.sendRedirect("/login");
                        return false;
                    }
                }
                //从redis中拿取用户信息
                boolean flag = redisHandler.exists(sessionIdNow);
                //存在就放行，否则回到登录界面
                if (flag)
                    return true;
            }
            httpServletResponse.sendRedirect("/login");
            return false;
        }catch (JedisConnectionException | RedisConnectionFailureException | ConnectException e){
            System.out.println("redis 连接失败");
            String cssStyle = "red message";
            String message = "redis connect fail";
            String status = "true";
            httpServletResponse.sendRedirect("/login?cssStyle="+cssStyle+"&message="+message+"&status="+status);
            return false;
        }
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
