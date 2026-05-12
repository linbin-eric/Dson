package cc.jfire.dson.paramtiez;

import cc.jfire.baseutil.reflect.TypeUtil;
import cc.jfire.dson.Dson;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Type;

public class WildcardUpperBoundDeserializeTest
{
    @Test
    public void shouldDeserializeWildcardUpperBoundAsBoundType()
    {
        Type type = new TypeUtil<Response<? extends Row>>()
        {
        }.getType();
        String body = """
                {
                  "data": {
                    "name": "alpha"
                  }
                }
                """;
        Response<? extends Row> response = Dson.fromString(type, body);
        Assert.assertEquals("alpha", response.data().getName());
    }

    public static class Response<T>
    {
        private T data;

        public T data()
        {
            return data;
        }
    }

    public static class Row
    {
        private String name;

        public String getName()
        {
            return name;
        }
    }
}
