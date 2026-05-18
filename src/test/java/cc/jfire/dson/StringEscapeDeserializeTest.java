package cc.jfire.dson;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class StringEscapeDeserializeTest
{
    private static final String ESCAPED_VALUE = "line1\\nline2\\t\\\"q\\\"\\\\slash\\r\\b\\f\\/tail\\u4e2d\\u6587";
    private static final String DECODED_VALUE = "line1\nline2\t\"q\"\\slash\r\b\f/tail中文";

    @Test
    public void stringEscapesAreDecoded()
    {
        for (DsonContext each : contexts())
        {
            assertEquals(DECODED_VALUE, each.fromString(String.class, "\"" + ESCAPED_VALUE + "\""));
        }
    }

    @Test
    public void objectStringEscapesAreDecoded()
    {
        String json = "{"
                      + "\"text\":\"" + ESCAPED_VALUE + "\","
                      + "\"array\":[\"a\\nb\",\"\\\\\",\"\\u4e2d\"],"
                      + "\"list\":[\"x\\ty\"],"
                      + "\"map\":{\"key\\n1\":\"value\\\"2\"},"
                      + "\"unknown\":{\"nested\":\"u\\u0041\"}"
                      + "}";
        for (DsonContext each : contexts())
        {
            EscapeData result = each.fromString(EscapeData.class, json);
            assertEquals(DECODED_VALUE, result.getText());
            assertArrayEquals(new String[]{"a\nb", "\\", "中"}, result.getArray());
            assertEquals(Arrays.asList("x\ty"), result.getList());
            assertEquals("value\"2", result.getMap().get("key\n1"));
            assertEquals("uA", ((Map<?, ?>) result.getUnknown()).get("nested"));
        }
    }

    @Test
    public void escapedStringsRoundTrip()
    {
        EscapeData data = new EscapeData();
        data.setText(DECODED_VALUE);
        data.setArray(new String[]{"a\nb", "\\", "\""});
        data.setList(Arrays.asList("x\ty", "中文"));
        data.setMap(new HashMap<>());
        data.getMap().put("key\n1", "value\"2");

        for (DsonContext each : contexts())
        {
            String     json   = each.toJson(data);
            EscapeData result = each.fromString(EscapeData.class, json);
            assertEquals(data.getText(), result.getText());
            assertArrayEquals(data.getArray(), result.getArray());
            assertEquals(data.getList(), result.getList());
            assertEquals(data.getMap(), result.getMap());
        }
    }

    private static List<DsonContext> contexts()
    {
        return Arrays.asList(
                new DsonContext(DsonConfig.STANDARD),
                new DsonContext(new DsonConfig().setReadUseCompile(true)),
                new DsonContext(new DsonConfig().setReadEntryUseCompile(true)),
                new DsonContext(new DsonConfig().setValueAccessorUseCompile(true)),
                new DsonContext(new DsonConfig().setWriteUseCompile(true)),
                new DsonContext(new DsonConfig().setReadUseCompile(true).setWriteUseCompile(true))
        );
    }

    @lombok.Data
    public static class EscapeData
    {
        private String              text;
        private String[]            array;
        private List<String>        list;
        private Map<String, String> map;
        private Object              unknown;
    }
}
