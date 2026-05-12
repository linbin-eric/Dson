package cc.jfire.dson.paramtiez;

import cc.jfire.baseutil.reflect.TypeUtil;
import cc.jfire.dson.Dson;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class GenericResponseByCompileDeserializeTest
{
    @Test
    public void shouldCompileDeserializeDirectParameterizedResponseType()
    {
        Type type = new TypeUtil<GenericResponse<Row>>()
        {
        }.getType();
        GenericResponse<Row> response = Dson.fromStringByCompile(type, """
                {
                  "errCode": 0,
                  "data": {
                    "name": "alpha"
                  }
                }
                """);
        Assert.assertEquals(Integer.valueOf(0), response.getErrCode());
        Assert.assertEquals("alpha", response.getData().getName());
    }

    @Test
    public void shouldCompileDeserializeBasicTypeArgument()
    {
        Type type = new TypeUtil<GenericResponse<String>>()
        {
        }.getType();
        GenericResponse<String> response = Dson.fromStringByCompile(type, """
                {
                  "errCode": 0,
                  "data": "alpha"
                }
                """);
        Assert.assertEquals(Integer.valueOf(0), response.getErrCode());
        Assert.assertEquals("alpha", response.getData());
    }

    @Test
    public void shouldCompileResolveInheritedTypeVariableInCollectionField()
    {
        RowListResponse response = Dson.fromStringByCompile(RowListResponse.class, """
                {
                  "errCode": 0,
                  "data": [
                    {
                      "name": "alpha"
                    },
                    {
                      "name": "beta"
                    }
                  ]
                }
                """);
        Assert.assertEquals(Integer.valueOf(0), response.getErrCode());
        Assert.assertEquals("alpha", response.getData().get(0).getName());
        Assert.assertEquals("beta", response.getData().get(1).getName());
    }

    @Test
    public void shouldCompileDeserializeNestedParameterizedTypeArgument()
    {
        Type type = new TypeUtil<GenericResponse<List<Map<String, Row>>>>()
        {
        }.getType();
        GenericResponse<List<Map<String, Row>>> response = Dson.fromStringByCompile(type, """
                {
                  "errCode": 0,
                  "data": [
                    {
                      "first": {
                        "name": "alpha"
                      }
                    }
                  ]
                }
                """);
        Assert.assertEquals(Integer.valueOf(0), response.getErrCode());
        Assert.assertEquals("alpha", response.getData().get(0).get("first").getName());
    }

    @Test
    public void shouldCompileDeserializeObjectContainingMultipleTypeVariables()
    {
        Type type = new TypeUtil<PairResponse<String, Row>>()
        {
        }.getType();
        PairResponse<String, Row> response = Dson.fromStringByCompile(type, """
                {
                  "errCode": 0,
                  "data": {
                    "left": "alpha",
                    "right": {
                      "name": "beta"
                    }
                  }
                }
                """);
        Assert.assertEquals(Integer.valueOf(0), response.getErrCode());
        Assert.assertEquals("alpha", response.getData().getLeft());
        Assert.assertEquals("beta", response.getData().getRight().getName());
    }

    @Test
    public void shouldCompileDeserializeWildcardUpperBoundTypeArgument()
    {
        Type type = new TypeUtil<GenericResponse<? extends Row>>()
        {
        }.getType();
        GenericResponse<? extends Row> response = Dson.fromStringByCompile(type, """
                {
                  "errCode": 0,
                  "data": {
                    "name": "alpha"
                  }
                }
                """);
        Assert.assertEquals(Integer.valueOf(0), response.getErrCode());
        Assert.assertEquals("alpha", response.getData().getName());
    }

    @Test
    public void shouldCompileNotReuseGenericArrayReaderAcrossDifferentTypeArguments()
    {
        StringArrayResponse stringResponse = Dson.fromStringByCompile(StringArrayResponse.class, """
                {
                  "errCode": 0,
                  "data": [
                    "alpha",
                    "beta"
                  ]
                }
                """);
        Assert.assertEquals(Integer.valueOf(0), stringResponse.getErrCode());
        Assert.assertArrayEquals(new String[]{"alpha", "beta"}, stringResponse.getData());

        RowArrayResponse rowResponse = Dson.fromStringByCompile(RowArrayResponse.class, """
                {
                  "errCode": 0,
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
        Assert.assertEquals(Integer.valueOf(0), rowResponse.getErrCode());
        Assert.assertEquals("gamma", rowResponse.getData()[0].getName());
        Assert.assertEquals("delta", rowResponse.getData()[1].getName());
    }

    public static class BaseResponse
    {
        private Integer errCode;

        public Integer getErrCode()
        {
            return errCode;
        }

        public void setErrCode(Integer errCode)
        {
            this.errCode = errCode;
        }
    }

    public static class GenericResponse<T> extends BaseResponse
    {
        private T data;

        public T getData()
        {
            return data;
        }

        public void setData(T data)
        {
            this.data = data;
        }
    }

    public abstract static class ListResponse<T> extends BaseResponse
    {
        private List<T> data;

        public List<T> getData()
        {
            return data;
        }

        public void setData(List<T> data)
        {
            this.data = data;
        }
    }

    public static class RowListResponse extends ListResponse<Row>
    {
    }

    public abstract static class ArrayResponse<T> extends BaseResponse
    {
        private T[] data;

        public T[] getData()
        {
            return data;
        }

        public void setData(T[] data)
        {
            this.data = data;
        }
    }

    public static class StringArrayResponse extends ArrayResponse<String>
    {
    }

    public static class RowArrayResponse extends ArrayResponse<Row>
    {
    }

    public static class PairResponse<L, R> extends GenericResponse<Pair<L, R>>
    {
    }

    public static class Pair<L, R>
    {
        private L left;
        private R right;

        public L getLeft()
        {
            return left;
        }

        public void setLeft(L left)
        {
            this.left = left;
        }

        public R getRight()
        {
            return right;
        }

        public void setRight(R right)
        {
            this.right = right;
        }
    }

    public static class Row
    {
        private String name;

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }
    }
}
