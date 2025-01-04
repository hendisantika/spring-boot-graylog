package id.my.hendisantika.graylog.interceptor;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.WebRequestHandlerInterceptorAdapter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
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

    //after the handler is executed
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response,
            Object handler, ModelAndView modelAndView)
            throws Exception {

        // calculate execution time
        long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;

        // get request ID
        long requestNumber = (Long) request.getAttribute("request-number");

        traceRequest(request, requestNumber);
        traceResponse(response, requestNumber, executeTime);
    }

    private void traceRequest(HttpServletRequest request, Long counter) throws IOException {
        ObjectNode req = nodeFactory.objectNode();
        req.put("request-name", "server-incoming-request");
        req.put("request-id", counter);
        req.put("uri", request.getRequestURI());
        req.put("method", request.getMethod());
        req.put("remote-address", request.getRemoteAddr());
        req.set("headers", mapToJsonNode(getRequestHeadersInfo(request)));

        log.debug(prettyJson ? jsonToPrettyString(req) : req.toString());
    }

    private static Map<String, String> getRequestHeadersInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();

        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }

    private void traceResponse(HttpServletResponse response, Long counter, long executeTime) throws IOException {
        ObjectNode resp = nodeFactory.objectNode();
        resp.put("request-name", "server-outgoing-response");
        resp.put("request-id", counter);
        resp.put("status-code", response.getStatus());
        resp.set("headers", mapToJsonNode(getResponseHeadersInfo(response)));
        resp.put("execution-time-ms", executeTime);

        log.debug(prettyJson ? jsonToPrettyString(resp) : resp.toString());
    }
}
