package cc.jfire.dson.paramtiez;

import cc.jfire.baseutil.reflect.TypeUtil;
import cc.jfire.dson.Dson;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.*;

public class GenericResponseDeserializeTest
{
    @Test
    public void shouldResolveInheritedTypeVariableInCollectionField()
    {
        String body = """
                {
                  "errCode": 0,
                  "data": [
                    {
                      "name": "alpha"
                    }
                  ]
                }
                """;
        TestRowsResponse response = Dson.fromString(TestRowsResponse.class, body);
        Assert.assertEquals(Integer.valueOf(0), response.getErrCode());
        Assert.assertEquals(1, response.rows().size());
        Assert.assertEquals("alpha", response.rows().get(0).getName());
    }

    @Test
    public void shouldResolveParameterizedObjectFieldWithTypeVariable()
    {
        String body = """
                {
                  "errCode": 0,
                  "data": {
                    "rows": [
                      {
                        "name": "alpha"
                      }
                    ],
                    "totalNum": 1
                  }
                }
                """;
        TestPagedResponse response = Dson.fromString(TestPagedResponse.class, body);
        Assert.assertEquals(Integer.valueOf(0), response.getErrCode());
        Assert.assertEquals(1, response.rows().size());
        Assert.assertEquals("alpha", response.rows().get(0).getName());
        Assert.assertEquals(Integer.valueOf(1), response.totalNum());
    }

    @Test
    public void shouldResolveTypeVariableThroughGenericMiddleClass()
    {
        String body = """
                {
                  "errCode": 0,
                  "data": [
                    {
                      "name": "alpha"
                    }
                  ]
                }
                """;
        A response = Dson.fromString(A.class, body);
        Assert.assertEquals(Integer.valueOf(0), response.getErrCode());
        Assert.assertEquals(1, response.rows().size());
        Assert.assertEquals("alpha", response.rows().get(0).getName());
    }

    @Test
    public void shouldDeserializeDirectParameterizedResponseType()
    {
        Type type = new TypeUtil<GenericResponse<TestRow>>()
        {
        }.getType();
        String body = """
                {
                  "errCode": 0,
                  "data": {
                    "name": "alpha"
                  }
                }
                """;
        GenericResponse<TestRow> response = Dson.fromString(type, body);
        Assert.assertEquals(Integer.valueOf(0), response.getErrCode());
        Assert.assertEquals("alpha", response.data().getName());
    }

    @Test
    public void shouldDeserializeBasicTypeArgument()
    {
        Type type = new TypeUtil<GenericResponse<String>>()
        {
        }.getType();
        String body = """
                {
                  "errCode": 0,
                  "data": "alpha"
                }
                """;
        GenericResponse<String> response = Dson.fromString(type, body);
        Assert.assertEquals(Integer.valueOf(0), response.getErrCode());
        Assert.assertEquals("alpha", response.data());
    }

    @Test
    public void shouldDeserializeIntegerTypeArgument()
    {
        Type type = new TypeUtil<GenericResponse<Integer>>()
        {
        }.getType();
        String body = """
                {
                  "errCode": 0,
                  "data": 12
                }
                """;
        GenericResponse<Integer> response = Dson.fromString(type, body);
        Assert.assertEquals(Integer.valueOf(0), response.getErrCode());
        Assert.assertEquals(Integer.valueOf(12), response.data());
    }

    @Test
    public void shouldDeserializeNestedParameterizedTypeArgument()
    {
        Type type = new TypeUtil<GenericResponse<List<Map<String, TestRow>>>>()
        {
        }.getType();
        String body = """
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
                """;
        GenericResponse<List<Map<String, TestRow>>> response = Dson.fromString(type, body);
        Assert.assertEquals(Integer.valueOf(0), response.getErrCode());
        Assert.assertEquals(1, response.data().size());
        Assert.assertEquals("alpha", response.data().get(0).get("first").getName());
    }

    @Test
    public void shouldDeserializeNestedParameterizedBasicTypeArgument()
    {
        Type type = new TypeUtil<GenericResponse<List<String>>>()
        {
        }.getType();
        String body = """
                {
                  "errCode": 0,
                  "data": [
                    "alpha",
                    "beta"
                  ]
                }
                """;
        GenericResponse<List<String>> response = Dson.fromString(type, body);
        Assert.assertEquals(Integer.valueOf(0), response.getErrCode());
        Assert.assertEquals(Arrays.asList("alpha", "beta"), response.data());
    }

    @Test
    public void shouldDeserializeRootCollectionWithParameterizedElement()
    {
        Type type = new TypeUtil<ArrayList<GenericResponse<TestRow>>>()
        {
        }.getType();
        String body = """
                [
                  {
                    "errCode": 0,
                    "data": {
                      "name": "alpha"
                    }
                  },
                  {
                    "errCode": 1,
                    "data": {
                      "name": "beta"
                    }
                  }
                ]
                """;
        ArrayList<GenericResponse<TestRow>> response = Dson.fromString(type, body);
        Assert.assertEquals(2, response.size());
        Assert.assertEquals(Integer.valueOf(0), response.get(0).getErrCode());
        Assert.assertEquals("alpha", response.get(0).data().getName());
        Assert.assertEquals(Integer.valueOf(1), response.get(1).getErrCode());
        Assert.assertEquals("beta", response.get(1).data().getName());
    }

    @Test
    public void shouldDeserializeRootCollectionWithParameterizedBasicElementField()
    {
        Type type = new TypeUtil<ArrayList<GenericResponse<String>>>()
        {
        }.getType();
        String body = """
                [
                  {
                    "errCode": 0,
                    "data": "alpha"
                  },
                  {
                    "errCode": 1,
                    "data": "beta"
                  }
                ]
                """;
        ArrayList<GenericResponse<String>> response = Dson.fromString(type, body);
        Assert.assertEquals(2, response.size());
        Assert.assertEquals(Integer.valueOf(0), response.get(0).getErrCode());
        Assert.assertEquals("alpha", response.get(0).data());
        Assert.assertEquals(Integer.valueOf(1), response.get(1).getErrCode());
        Assert.assertEquals("beta", response.get(1).data());
    }

    @Test
    public void shouldDeserializeRootMapWithParameterizedValue()
    {
        Type type = new TypeUtil<HashMap<String, GenericResponse<TestRow>>>()
        {
        }.getType();
        String body = """
                {
                  "first": {
                    "errCode": 0,
                    "data": {
                      "name": "alpha"
                    }
                  },
                  "second": {
                    "errCode": 1,
                    "data": {
                      "name": "beta"
                    }
                  }
                }
                """;
        HashMap<String, GenericResponse<TestRow>> response = Dson.fromString(type, body);
        Assert.assertEquals(Integer.valueOf(0), response.get("first").getErrCode());
        Assert.assertEquals("alpha", response.get("first").data().getName());
        Assert.assertEquals(Integer.valueOf(1), response.get("second").getErrCode());
        Assert.assertEquals("beta", response.get("second").data().getName());
    }

    @Test
    public void shouldResolveBasicTypeVariableInInheritedCollectionField()
    {
        String body = """
                {
                  "errCode": 0,
                  "data": [
                    "alpha",
                    "beta"
                  ]
                }
                """;
        StringRowsResponse response = Dson.fromString(StringRowsResponse.class, body);
        Assert.assertEquals(Integer.valueOf(0), response.getErrCode());
        Assert.assertEquals(Arrays.asList("alpha", "beta"), response.rows());
    }

    @Test
    public void shouldResolveMapKeyAndValueTypeVariables()
    {
        String body = """
                {
                  "errCode": 0,
                  "data": {
                    "first": {
                      "name": "alpha"
                    }
                  }
                }
                """;
        StringRowMapResponse response = Dson.fromString(StringRowMapResponse.class, body);
        Assert.assertEquals(Integer.valueOf(0), response.getErrCode());
        Assert.assertEquals("alpha", response.data().get("first").getName());
    }

    @Test
    public void shouldResolveBasicMapValueTypeVariable()
    {
        String body = """
                {
                  "errCode": 0,
                  "data": {
                    "first": 12,
                    "second": 24
                  }
                }
                """;
        StringIntegerMapResponse response = Dson.fromString(StringIntegerMapResponse.class, body);
        Assert.assertEquals(Integer.valueOf(0), response.getErrCode());
        Assert.assertEquals(Integer.valueOf(12), response.data().get("first"));
        Assert.assertEquals(Integer.valueOf(24), response.data().get("second"));
    }

    @Test
    public void shouldDeserializeArrayTypeArgument()
    {
        Type type = new TypeUtil<GenericResponse<TestRow[]>>()
        {
        }.getType();
        String body = """
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
                """;
        GenericResponse<TestRow[]> response = Dson.fromString(type, body);
        Assert.assertEquals(Integer.valueOf(0), response.getErrCode());
        Assert.assertEquals(2, response.data().length);
        Assert.assertEquals("alpha", response.data()[0].getName());
        Assert.assertEquals("beta", response.data()[1].getName());
    }

    @Test
    public void shouldDeserializeParameterizedComponentArrayTypeArgument()
    {
        Type type = new TypeUtil<GenericResponse<List<TestRow>[]>>()
        {
        }.getType();
        String body = """
                {
                  "errCode": 0,
                  "data": [
                    [
                      {
                        "name": "alpha"
                      }
                    ],
                    [
                      {
                        "name": "beta"
                      }
                    ]
                  ]
                }
                """;
        GenericResponse<List<TestRow>[]> response = Dson.fromString(type, body);
        Assert.assertEquals(Integer.valueOf(0), response.getErrCode());
        Assert.assertEquals(2, response.data().length);
        Assert.assertEquals("alpha", response.data()[0].get(0).getName());
        Assert.assertEquals("beta", response.data()[1].get(0).getName());
    }

    @Test
    public void shouldResolveGenericArrayField()
    {
        String body = """
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
                """;
        TestRowArrayResponse response = Dson.fromString(TestRowArrayResponse.class, body);
        Assert.assertEquals(Integer.valueOf(0), response.getErrCode());
        Assert.assertEquals(2, response.data().length);
        Assert.assertEquals("alpha", response.data()[0].getName());
        Assert.assertEquals("beta", response.data()[1].getName());
    }

    @Test
    public void shouldResolveBasicGenericArrayField()
    {
        String body = """
                {
                  "errCode": 0,
                  "data": [
                    "alpha",
                    "beta"
                  ]
                }
                """;
        StringArrayResponse response = Dson.fromString(StringArrayResponse.class, body);
        Assert.assertEquals(Integer.valueOf(0), response.getErrCode());
        Assert.assertArrayEquals(new String[]{"alpha", "beta"}, response.data());
    }

    @Test
    public void shouldDeserializeRootGenericArrayWithParameterizedComponent()
    {
        Type type = new TypeUtil<GenericResponse<TestRow>[]>()
        {
        }.getType();
        String body = """
                [
                  {
                    "errCode": 0,
                    "data": {
                      "name": "alpha"
                    }
                  },
                  {
                    "errCode": 1,
                    "data": {
                      "name": "beta"
                    }
                  }
                ]
                """;
        GenericResponse<TestRow>[] response = Dson.fromString(type, body);
        Assert.assertEquals(2, response.length);
        Assert.assertEquals(Integer.valueOf(0), response[0].getErrCode());
        Assert.assertEquals("alpha", response[0].data().getName());
        Assert.assertEquals(Integer.valueOf(1), response[1].getErrCode());
        Assert.assertEquals("beta", response[1].data().getName());
    }

    @Test
    public void shouldDeserializeObjectContainingMultipleTypeVariables()
    {
        Type type = new TypeUtil<PairResponse<TestRow, TestRow>>()
        {
        }.getType();
        String body = """
                {
                  "errCode": 0,
                  "data": {
                    "left": {
                      "name": "alpha"
                    },
                    "right": {
                      "name": "beta"
                    }
                  }
                }
                """;
        PairResponse<TestRow, TestRow> response = Dson.fromString(type, body);
        Assert.assertEquals(Integer.valueOf(0), response.getErrCode());
        Assert.assertEquals("alpha", response.data().left().getName());
        Assert.assertEquals("beta", response.data().right().getName());
    }

    @Test
    public void shouldDeserializeObjectContainingBasicTypeVariable()
    {
        Type type = new TypeUtil<PairResponse<String, TestRow>>()
        {
        }.getType();
        String body = """
                {
                  "errCode": 0,
                  "data": {
                    "left": "alpha",
                    "right": {
                      "name": "beta"
                    }
                  }
                }
                """;
        PairResponse<String, TestRow> response = Dson.fromString(type, body);
        Assert.assertEquals(Integer.valueOf(0), response.getErrCode());
        Assert.assertEquals("alpha", response.data().left());
        Assert.assertEquals("beta", response.data().right().getName());
    }

    @Test
    public void shouldDeserializeWildcardUpperBoundTypeArgument()
    {
        Type type = new TypeUtil<GenericResponse<? extends TestRow>>()
        {
        }.getType();
        String body = """
                {
                  "errCode": 0,
                  "data": {
                    "name": "alpha"
                  }
                }
                """;
        GenericResponse<? extends TestRow> response = Dson.fromString(type, body);
        Assert.assertEquals(Integer.valueOf(0), response.getErrCode());
        Assert.assertEquals("alpha", response.data().getName());
    }

    public static class BaseResponse
    {
        private Integer errCode;

        public Integer getErrCode()
        {
            return errCode;
        }
    }

    public static class GenericResponse<T> extends BaseResponse
    {
        private T data;

        public T data()
        {
            return data;
        }
    }

    public abstract static class RowsResponse<T> extends BaseResponse
    {
        private List<T> data;

        public List<T> rows()
        {
            return data;
        }
    }

    public abstract static class PagedRowsResponse<T> extends BaseResponse
    {
        private PageData<T> data;

        public List<T> rows()
        {
            return data.getRows();
        }

        public Integer totalNum()
        {
            return data.getTotalNum();
        }
    }

    public static class PageData<T>
    {
        private List<T> rows;
        private Integer totalNum;

        public List<T> getRows()
        {
            return rows;
        }

        public Integer getTotalNum()
        {
            return totalNum;
        }
    }

    public static class TestRowsResponse extends RowsResponse<TestRow>
    {
    }

    public static class StringRowsResponse extends RowsResponse<String>
    {
    }

    public static class TestPagedResponse extends PagedRowsResponse<TestRow>
    {
    }

    public static class A extends B<TestRow>
    {
    }

    public static class StringRowMapResponse extends MapResponse<String, TestRow>
    {
    }

    public static class StringIntegerMapResponse extends MapResponse<String, Integer>
    {
    }

    public static class TestRowArrayResponse extends ArrayResponse<TestRow>
    {
    }

    public static class StringArrayResponse extends ArrayResponse<String>
    {
    }

    public abstract static class B<T> extends C<T, String>
    {
    }

    public abstract static class C<K, V> extends BaseResponse
    {
        private List<K> data;

        public List<K> rows()
        {
            return data;
        }
    }

    public abstract static class MapResponse<K, V> extends BaseResponse
    {
        private Map<K, V> data;

        public Map<K, V> data()
        {
            return data;
        }
    }

    public abstract static class ArrayResponse<T> extends BaseResponse
    {
        private T[] data;

        public T[] data()
        {
            return data;
        }
    }

    public static class PairResponse<L, R> extends GenericResponse<Pair<L, R>>
    {
    }

    public static class Pair<L, R>
    {
        private L left;
        private R right;

        public L left()
        {
            return left;
        }

        public R right()
        {
            return right;
        }
    }

    public class ListA extends ArrayList<String>
    {
    }

    public class ListB extends ArrayList<Integer>
    {
    }

    public static class TestRow
    {
        private String name;

        public String getName()
        {
            return name;
        }
    }
}
