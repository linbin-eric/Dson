package cc.jfire.dson.paramtiez;

import cc.jfire.baseutil.reflect.TypeUtil;
import cc.jfire.dson.Dson;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class ReaderCachePollutionTest
{
    @Test
    public void shouldNotPolluteBasicReaderAfterGenericResponseWithBasicTypeArgument()
    {
        Type stringResponseType = new TypeUtil<Response<String>>()
        {
        }.getType();
        Response<String> stringResponse = Dson.fromString(stringResponseType, """
                {
                  "data": "alpha"
                }
                """);
        Assert.assertEquals("alpha", stringResponse.data());

        Type listStringResponseType = new TypeUtil<Response<List<String>>>()
        {
        }.getType();
        Response<List<String>> listResponse = Dson.fromString(listStringResponseType, """
                {
                  "data": [
                    "beta",
                    "gamma"
                  ]
                }
                """);
        Assert.assertEquals(Arrays.asList("beta", "gamma"), listResponse.data());

        StringRowsResponse rowsResponse = Dson.fromString(StringRowsResponse.class, """
                {
                  "data": [
                    "delta",
                    "epsilon"
                  ]
                }
                """);
        Assert.assertEquals(Arrays.asList("delta", "epsilon"), rowsResponse.rows());
    }

    @Test
    public void shouldNotReuseObjectReaderFromPreviousTypeArgument()
    {
        Type rowResponseType = new TypeUtil<Response<Row>>()
        {
        }.getType();
        Response<Row> rowResponse = Dson.fromString(rowResponseType, """
                {
                  "data": {
                    "name": "alpha"
                  }
                }
                """);
        Assert.assertEquals("alpha", rowResponse.data().getName());

        Type stringResponseType = new TypeUtil<Response<String>>()
        {
        }.getType();
        Response<String> stringResponse = Dson.fromString(stringResponseType, """
                {
                  "data": "beta"
                }
                """);
        Assert.assertEquals("beta", stringResponse.data());
    }

    public static class Response<T>
    {
        private T data;

        public T data()
        {
            return data;
        }
    }

    public abstract static class RowsResponse<T>
    {
        private List<T> data;

        public List<T> rows()
        {
            return data;
        }
    }

    public static class StringRowsResponse extends RowsResponse<String>
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
