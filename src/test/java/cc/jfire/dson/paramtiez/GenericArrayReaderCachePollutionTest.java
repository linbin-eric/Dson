package cc.jfire.dson.paramtiez;

import cc.jfire.dson.Dson;
import org.junit.Assert;
import org.junit.Test;

public class GenericArrayReaderCachePollutionTest
{
    @Test
    public void shouldNotReuseGenericArrayReaderAcrossDifferentTypeArguments()
    {
        StringArrayResponse stringResponse = Dson.fromString(StringArrayResponse.class, """
                {
                  "data": [
                    "alpha",
                    "beta"
                  ]
                }
                """);
        Assert.assertArrayEquals(new String[]{"alpha", "beta"}, stringResponse.data());

        RowArrayResponse rowResponse = Dson.fromString(RowArrayResponse.class, """
                {
                  "data": [
                    {
                      "name": "gamma"
                    },
                    {
                      "name": "delta"
                    }
                  ]
                }
                """);
        Assert.assertEquals("gamma", rowResponse.data()[0].getName());
        Assert.assertEquals("delta", rowResponse.data()[1].getName());
    }

    public abstract static class ArrayResponse<T>
    {
        private T[] data;

        public T[] data()
        {
            return data;
        }
    }

    public static class StringArrayResponse extends ArrayResponse<String>
    {
    }

    public static class RowArrayResponse extends ArrayResponse<Row>
    {
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
