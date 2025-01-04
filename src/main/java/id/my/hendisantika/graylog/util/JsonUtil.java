package id.my.hendisantika.graylog.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-graylog
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 05/01/25
 * Time: 05.57
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
public class JsonUtil {

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    public static String jsonToPrettyString(Object node) {
        try {
            return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
        } catch (JsonProcessingException e) {
            log.error("Exception during parsing JSON: {}", e.getMessage());
            return "{ \"error\": \"JsonProcessingException\" }";
        }
    }

    public static JsonNode mapToJsonNode(Map map) {
        return jsonMapper.convertValue(map, JsonNode.class);
    }
}
