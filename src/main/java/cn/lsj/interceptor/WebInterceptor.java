package cn.lsj.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebInterceptor implements HandlerInterceptor {

    /**
     * 在请求处理之前进行调用，在进入Controller方法之前先进入此方法
     * 返回true才会继续向下执行，返回false取消当前请求
     * 登录拦截，权限资源控制工作
     * */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
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
