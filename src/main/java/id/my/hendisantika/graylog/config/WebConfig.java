package id.my.hendisantika.graylog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-graylog
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 05/01/25
 * Time: 05.54
 * To change this template use File | Settings | File Templates.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final RestControllerInterceptor restControllerInterceptor;

    public WebConfig(RestControllerInterceptor restControllerInterceptor) {
        this.restControllerInterceptor = restControllerInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(restControllerInterceptor).addPathPatterns("/**");
    }
}
