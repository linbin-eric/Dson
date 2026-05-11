package cc.jfire.dson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DsonToJsonObjectTest
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @SuppressWarnings("unchecked")
    @Test
    public void toJsonObject()
    {
        JsonObjectData data   = new JsonObjectData();
        Object         result = Dson.toJsonObject(data);

        assertTrue(result instanceof Map);
        Map<String, Object> map = (Map<String, Object>) result;
        assertEquals("dson", map.get("name"));
        assertEquals(7, map.get("age"));
        assertEquals(Arrays.asList("fast", "json"), map.get("tags"));
        assertEquals(Map.of("language", "java"), map.get("attributes"));

        Object child = map.get("child");
        assertTrue(child instanceof Map);
        assertEquals("nested", ((Map<String, Object>) child).get("name"));
    }

    @Test
    public void toJsonObjectWithNull()
    {
        assertNull(Dson.toJsonObject(null));
    }

    @Test
    public void toJsonObjectMatchesToJson() throws Exception
    {
        JsonObjectData data = new JsonObjectData();

        JsonNode fromToJson       = MAPPER.readTree(Dson.toJson(data));
        JsonNode fromToJsonObject = MAPPER.readTree(Dson.toJson(Dson.toJsonObject(data)));

        assertEquals(fromToJson, fromToJsonObject);
    }

    static class JsonObjectData
    {
        private final String              name       = "dson";
        private final int                 age        = 7;
        private final List<String>        tags       = Arrays.asList("fast", "json");
        private final Map<String, String> attributes = new HashMap<>(Map.of("language", "java"));
        private final JsonObjectChild     child      = new JsonObjectChild();

        public String getName()
        {
            return name;
        }

        public int getAge()
        {
            return age;
        }

        public List<String> getTags()
        {
            return tags;
        }

        public Map<String, String> getAttributes()
        {
            return attributes;
        }

        public JsonObjectChild getChild()
        {
            return child;
        }
    }

    static class JsonObjectChild
    {
        private final String name = "nested";

        public String getName()
        {
            return name;
        }
    }
}
