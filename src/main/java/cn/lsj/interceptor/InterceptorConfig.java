package cn.lsj.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@ComponentScan
public class InterceptorConfig extends WebMvcConfigurerAdapter{

    /** 解决拦截器中无法注入bean的问题 */
    @Bean
    LoginInterceptor localInterceptor() {
        return new LoginInterceptor();
    }

    /**
     * 静态文件映射
     * addResourceHandler指的是对外暴露的访问路径，addResourceLocations指的是文件放置的目录
     * 映射之后，Controller返回到 html ，使用映射后的路径
     * 如下将 static 下的静态文件映射到根路径下，
     * 文件实际路径 /static/semantic/semantic.min.js 映射之后
     * 在 html 中就可以通过 semantic/semantic.min.js 路径访问静态文件
     *
     * 也可映射绝对路径,例如下面注释，把D盘static中的静态文件，映射到项目根路径下
     * */

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
       // registry.addResourceHandler("/**").addResourceLocations("D:/static/");
        registry.addResourceHandler("/static/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX+"/static/");
        registry.addResourceHandler("/templates/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX+"/templates/");
        super.addResourceHandlers(registry);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截规则：除了login和静态资源，其他都拦截判断
        registry.addInterceptor(localInterceptor()).addPathPatterns("/**").excludePathPatterns("/login","/static","/static/**","/error","/test/**");
        super.addInterceptors(registry);
    }

}
