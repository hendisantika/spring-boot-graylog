package id.my.hendisantika.graylog.interceptor;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.WebRequestHandlerInterceptorAdapter;

import java.util.concurrent.atomic.AtomicLong;

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
@Slf4j
@Component
public class RestControllerInterceptor extends WebRequestHandlerInterceptorAdapter {

    private final AtomicLong counter = new AtomicLong();

    private final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

    @Value("${json.log.pretty}")
    private boolean prettyJson;

    //before the actual handler will be executed
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        request.setAttribute("request-number", counter.incrementAndGet());

        return true;
    }
}
