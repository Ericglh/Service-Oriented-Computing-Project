package util;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by htyleo on 3/31/17.
 */
public class JsonUtil {

    public static String getAsString(JsonNode jsonNode, String key) {
        JsonNode value = jsonNode.get(key);
        return value == null ? null : value.asText();
    }

    public static Integer getAsInt(JsonNode jsonNode, String key) {
        JsonNode value = jsonNode.get(key);
        return value == null ? null : value.asInt();
    }

}
