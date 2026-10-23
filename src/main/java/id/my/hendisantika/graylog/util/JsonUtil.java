package id.my.hendisantika.graylog.util;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

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

    private static final ObjectMapper jsonMapper = JsonMapper.builder().build();

    public static String jsonToPrettyString(Object node) {
        try {
            return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
        } catch (JacksonException e) {
            log.error("Exception during parsing JSON: {}", e.getMessage());
            return "{ \"error\": \"JacksonException\" }";
        }
    }

    public static JsonNode mapToJsonNode(Map map) {
        return jsonMapper.convertValue(map, JsonNode.class);
    }
}
