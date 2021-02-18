package com.mscg;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonCleaner {

    public static int sumValues(String source) {
        var pattern = Pattern.compile("(-?\\d+)");
        var matcher = pattern.matcher(source);

        int sum = 0;
        while (matcher.find()) {
            int val = Integer.parseInt(matcher.group(1));
            sum += val;
        }

        return sum;
    }

    public static String cleanJson(String source) throws Exception {
        var mapper = new ObjectMapper();
        var rootNode = mapper.readTree(source);
        if (isToRemove(rootNode)) {
           return ""; 
        }

        for (var it = rootNode.iterator(); it.hasNext();) {
            clean(it);
        }
        return mapper.writeValueAsString(rootNode);
    }

    private static void clean(Iterator<JsonNode> iterator) {
        JsonNode node = iterator.next();
        if (node instanceof ArrayNode arr) {
            for (var it = arr.iterator(); it.hasNext(); ) {
                clean(it);
            }
        } else if (node instanceof ObjectNode obj) {
            if (isToRemove(obj)) {
                iterator.remove();
                return;
            }
            for (var it = obj.iterator(); it.hasNext(); ) {
                clean(it);
            }
        }
    }

    private static boolean isToRemove(JsonNode node) {
        if (node instanceof ObjectNode obj) {
            for (var it = obj.fields(); it.hasNext();) {
                Entry<String, JsonNode> field = it.next();
                if (field.getValue() instanceof TextNode t && "red".equalsIgnoreCase(t.asText())) {
                    return true;
                }
            }
        }
        return false;
    }

}
