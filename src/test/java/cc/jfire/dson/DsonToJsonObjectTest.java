package cc.jfire.dson;

import cc.jfire.dson.dynamic.DynamicJsonValue;
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
    public void toJsonValue()
    {
        assertJsonObjectData(Dson.toJsonValue(new JsonObjectData()));
    }

    @Test
    public void toJsonValueByCompile()
    {
        assertJsonObjectData(Dson.toJsonValueByCompile(new JsonObjectData()));
    }

    @Test
    public void dynamicJsonValue()
    {
        assertEquals(dynamicValue("root", 0), Dson.toJsonValue(new DynamicNode("root", 0)));
    }

    @Test
    public void dynamicJsonValueByCompile()
    {
        assertEquals(dynamicValue("root", 0), Dson.toJsonValueByCompile(new DynamicNode("root", 0)));
    }

    @Test
    public void dynamicJsonValueWithDynamicJsonValueProperty()
    {
        assertDynamicParentJsonValue(Dson.toJsonValue(new DynamicParentNode("parent", 9, new DynamicNode("child", 10))));
    }

    @Test
    public void dynamicJsonValueWithDynamicJsonValuePropertyByCompile()
    {
        assertDynamicParentJsonValue(Dson.toJsonValueByCompile(new DynamicParentNode("parent", 9, new DynamicNode("child", 10))));
    }

    @Test
    public void nestedDynamicJsonValue()
    {
        assertNestedDynamicJsonValue(Dson.toJsonValue(new NestedDynamicData()));
    }

    @Test
    public void nestedDynamicJsonValueByCompile()
    {
        assertNestedDynamicJsonValue(Dson.toJsonValueByCompile(new NestedDynamicData()));
    }

    @SuppressWarnings("unchecked")
    private void assertJsonObjectData(Object result)
    {
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

    @SuppressWarnings("unchecked")
    private void assertDynamicParentJsonValue(Object result)
    {
        assertTrue(result instanceof Map);
        Map<String, Object> map = (Map<String, Object>) result;
        assertEquals("dynamic-parent", map.get("type"));
        assertEquals("parent", map.get("name"));
        assertEquals(9, map.get("level"));
        assertEquals(dynamicValue("child", 10), map.get("child"));
    }

    @SuppressWarnings("unchecked")
    private void assertNestedDynamicJsonValue(Object result)
    {
        assertTrue(result instanceof Map);
        Map<String, Object> map = (Map<String, Object>) result;
        assertEquals(dynamicValue("direct", 1), map.get("direct"));
        assertEquals(dynamicValue("interface", 2), map.get("interfaceValue"));
        assertEquals(dynamicValue("object", 3), map.get("objectValue"));
        assertEquals(Arrays.asList(dynamicValue("list-a", 4), dynamicValue("list-b", 5)), map.get("list"));
        assertEquals(Arrays.asList(dynamicValue("array", 6)), map.get("array"));

        Object mapValue = map.get("map");
        assertTrue(mapValue instanceof Map);
        assertEquals(dynamicValue("map", 7), ((Map<String, Object>) mapValue).get("map"));

        Object nested = map.get("nested");
        assertTrue(nested instanceof Map);
        assertEquals(dynamicValue("child", 8), ((Map<String, Object>) nested).get("child"));
    }

    private static Map<String, Object> dynamicValue(String name, int level)
    {
        return Map.of("type", "dynamic", "name", name, "level", level);
    }

    @Test
    public void toJsonValueWithNull()
    {
        assertNull(Dson.toJsonValue(null));
    }

    @Test
    public void toJsonValueByCompileWithNull()
    {
        assertNull(Dson.toJsonValueByCompile(null));
    }

    @Test
    public void toJsonObjectMatchesToJson() throws Exception
    {
        JsonObjectData data = new JsonObjectData();

        JsonNode fromToJson       = MAPPER.readTree(Dson.toJson(data));
        JsonNode fromToJsonObject = MAPPER.readTree(Dson.toJson(Dson.toJsonValue(data)));

        assertEquals(fromToJson, fromToJsonObject);
    }

    @Test
    public void toJsonObjectByCompileMatchesToJsonByCompile() throws Exception
    {
        JsonObjectData data = new JsonObjectData();

        JsonNode fromToJson       = MAPPER.readTree(Dson.toJsonByCompile(data));
        JsonNode fromToJsonObject = MAPPER.readTree(Dson.toJsonByCompile(Dson.toJsonValueByCompile(data)));

        assertEquals(fromToJson, fromToJsonObject);
    }

    public static class JsonObjectData
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

    public static class JsonObjectChild
    {
        private final String name = "nested";

        public String getName()
        {
            return name;
        }
    }

    public static final class DynamicNode implements DynamicJsonValue
    {
        private final String name;
        private final int    level;

        private DynamicNode(String name, int level)
        {
            this.name  = name;
            this.level = level;
        }

        @Override
        public Object toJsonValue()
        {
            return dynamicValue(name, level);
        }
    }

    public static final class DynamicParentNode implements DynamicJsonValue
    {
        private final String           name;
        private final int              level;
        private final DynamicJsonValue child;

        private DynamicParentNode(String name, int level, DynamicJsonValue child)
        {
            this.name  = name;
            this.level = level;
            this.child = child;
        }

        public DynamicJsonValue getChild()
        {
            return child;
        }

        @Override
        public Object toJsonValue()
        {
            return Map.of("type", "dynamic-parent", "name", name, "level", level, "child", child.toJsonValue());
        }
    }

    public static final class NestedDynamicData
    {
        private final DynamicNode      direct         = new DynamicNode("direct", 1);
        private final DynamicJsonValue interfaceValue = new DynamicNode("interface", 2);
        private final Object           objectValue    = new DynamicNode("object", 3);
        private final List<DynamicNode> list           = Arrays.asList(new DynamicNode("list-a", 4), new DynamicNode("list-b", 5));
        private final DynamicNode[]    array          = new DynamicNode[] { new DynamicNode("array", 6) };
        private final Map<String, DynamicNode> map    = new HashMap<>(Map.of("map", new DynamicNode("map", 7)));
        private final NestedDynamicChild nested        = new NestedDynamicChild();

        public DynamicNode getDirect()
        {
            return direct;
        }

        public DynamicJsonValue getInterfaceValue()
        {
            return interfaceValue;
        }

        public Object getObjectValue()
        {
            return objectValue;
        }

        public List<DynamicNode> getList()
        {
            return list;
        }

        public DynamicNode[] getArray()
        {
            return array;
        }

        public Map<String, DynamicNode> getMap()
        {
            return map;
        }

        public NestedDynamicChild getNested()
        {
            return nested;
        }
    }

    public static final class NestedDynamicChild
    {
        private final DynamicNode child = new DynamicNode("child", 8);

        public DynamicNode getChild()
        {
            return child;
        }
    }
}
