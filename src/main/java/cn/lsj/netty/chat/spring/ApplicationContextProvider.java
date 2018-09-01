package cn.lsj.netty.chat.spring;

import cn.lsj.netty.chat.WebSocketFrameHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Auther: Lushunjian
 * @Date: 2018/9/1 20:31
 * @Description:
 */
@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    /**非@import显式注入，则@Component是必须的，且该类必须与main同包或子包
     * 若非同包或子包，则需手动import 注入，不需要@Component注解
     * */

    private static ApplicationContext applicationContext = null;

    //通过name获取 Bean.
    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz){
        return applicationContext.getBean(clazz);
    }

    public static <T> T getBeans(Class<T> clazz){
        return applicationContext.getBean(clazz);
    }

    //通过name,以及Clazz返回指定的Bean
    public static <T extends WebSocketFrameHandler> T getBean(String name, Class<T> clazz){
        return applicationContext.getBean(name, clazz);
    }

    private static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        ApplicationContextProvider.applicationContext = applicationContext;
    }
}
