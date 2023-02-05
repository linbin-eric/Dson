package com.jfirer.dson;

import com.jfirer.baseutil.reflect.TypeUtil;
import com.jfirer.dson.model.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FunctionTest extends Support
{
    @Test
    public void rightTest()
    {
        String string = Dson.toJson(data);
        logger.debug("输出的json是\n\n{}\r\n\n", string);
        assertTrue(data.equal(Dson.fromString(Data.class, string)));
        logger.debug("输出的数组json是\n\n{}\r\n\n", Dson.toJson(new Data[]{data, data}));
        Data[][] origin = new Data[][]{{data, data}, {data, data, data}};
        string = Dson.toJson(origin);
        Data[][] result = Dson.fromString(Data[][].class, string);
        for (int i = 0; i < origin.length; i++)
        {
            for (int j = 0; j < origin[i].length; j++)
            {
                assertTrue(origin[i][j].equal(result[i][j]));
            }
        }
        Data[] test1 = new Data[]{data, data};
        string = Dson.toJson(test1);
        Data[] test1Result = Dson.fromString(Data[].class, string);
        for (int i = 0; i < test1.length; i++)
        {
            assertTrue(test1[i].equal(test1Result[i]));
        }
        Data[][][] test2 = new Data[][][]{{{data, data}, {data}}, {{data, data, data, data}, {data}, {data}}};
        string = Dson.toJson(test2);
        Data[][][] test2Result = Dson.fromString(Data[][][].class, string);
        for (int i = 0; i < test2Result.length; i++)
        {
            for (int j = 0; j < test2Result[i].length; j++)
            {
                for (int k = 0; k < test2Result[i][j].length; k++)
                {
                    assertTrue(test2Result[i][j][k].equal(test2[i][j][k]));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void typeTest()
    {
        ArrayList<String> list = new ArrayList<String>();
        list.add("sdadasd");
        list.add("sdadsasda");
        String value = Dson.toJson(list);
        ArrayList<String> result = (ArrayList<String>) Dson.fromString(new TypeUtil<ArrayList<String>>()
        {}.getType(), value);
        assertTrue(list.equals(result));
        ArrayList<NestData> arrayList = new ArrayList<NestData>();
        NestData            cdata     = new NestData();
        cdata.setAge(1212);
        cdata.setName("sdasdas");
        arrayList.add(cdata);
        cdata = new NestData();
        cdata.setAge(1212121);
        cdata.setName("dassdas");
        arrayList.add(cdata);
        value = Dson.toJson(arrayList);
        System.out.println(value);
        ArrayList<NestData> result1 = Dson.fromString(new TypeUtil<ArrayList<NestData>>()
        {}.getType(), value);
        assertTrue(arrayList.equals(result1));
        ArrayList<Data> list2 = new ArrayList<Data>();
        list2.add(data);
        list2.add(data);
        value = Dson.toJson(list2);
        System.out.println(value);
        ArrayList<Data> result3 = Dson.fromString(new TypeUtil<ArrayList<Data>>()
        {}.getType(), value);
        assertTrue(list2.get(0).equal(result3.get(0)) && list2.get(1).equal(result3.get(1)));
        HashMap<String, Data> map = new HashMap<String, Data>();
        map.put("12wq", data);
        map.put("xczc", data);
        value = Dson.toJson(map);
        HashMap<String, Data> result4 = Dson.fromString(new TypeUtil<HashMap<String, Data>>()
        {}.getType(), value);
        assertTrue(map.get("12wq").equal(result4.get("12wq")));
        assertTrue(map.get("xczc").equal(result4.get("xczc")));
    }

    @Test
    public void test1()
    {
        assertEquals("{\"data\":{\"1\":\"121212\"}}", Dson.toJson(new FunctionData7()));
    }

    @Test
    public void test2()
    {
        System.out.println(Dson.toJson(new FunctionData10()));
    }

    @Test
    public void test3()
    {
        System.out.println(Dson.toJson(new FunctionData13()));
        assertEquals("{\"array1\":[\"1212\",\"12112\"],\"array2\":[1,2,3],\"array3\":[true,false],\"array4\":[\"c\",\"d\"],\"array5\":[1,2,3,4,5,7],\"array6\":[1221121231231,212312313],\"array7\":[2.36,5.698],\"array8\":[2323.231,2323.2313123],\"array9\":[100,23]}", Dson.toJson(new FunctionData13()));
    }

    @Test
    public void enumTest()
    {
        FunData16 data16 = new FunData16();
        data16.setTest(TestEnum.PUSH);
        assertEquals("{\"test\":\"PUSH\"}", Dson.toJson(data16));
    }

    @Test
    public void definitionTest()
    {
        FunctionData16 data16 = new FunctionData16();
        data16.setName("111");
        assertEquals("{\"name\":\"123\"}", Dson.toJson(data16));
    }

    @Test
    public void readStringTest()
    {
        FunctionData17 data17   = new FunctionData17();
        BaseData       baseData = new BaseData();
        data17.setValue(baseData);
        String         json  = Dson.toJson(data17);
        FunctionData17 o     = Dson.fromString(FunctionData17.class, json);
        String         value = (String) o.getValue();
        assertEquals(value, Dson.toJson(baseData));
    }

    @Test
    public void test4()
    {
        String content = """
                         {
                           "b" : 2.3599999999999999,
                           "e1" : "sdasdasd",
                           "c" : 5.6986999999999997,
                           "chars" : [
                             "a",
                             "b"
                           ],
                           "h1" : 12,
                           "d" : 121212121212,
                           "datas" : [
                             {
                               "age" : 13,
                               "name" : "sdasda"
                             },
                             {
                               "age" : 20,
                               "name" : "dasdas"
                             }
                           ],
                           "lists" : [
                             [
                               "dasdasda",
                               "dasdasdasdasdasd"
                             ],
                             [
                               "1212121dasdasdasdasdasd",
                               "dasdasd1212121212asdasdasd"
                             ]
                           ],
                           "e" : "f",
                           "array1" : [
                             1,
                             2,
                             3,
                             4,
                             5,
                             65
                           ],
                           "nestData" : {
                             "age" : 26,
                             "name" : "dsadas"
                           },
                           "f" : true,
                           "c1" : 2323.34234234,
                           "f1" : true,
                           "g" : 5,
                           "nestDatas" : [
                             {
                               "age" : 12,
                               "name" : "das"
                             },
                             {
                               "age" : 1222,
                               "name" : "daasdadasd"
                             }
                           ],
                           "array3" : [
                             1,
                             2,
                             3,
                             4,
                             5,
                             6,
                             7
                           ],
                           "h" : 3,
                           "map" : {
                             "dsada" : "你好",
                             "恁大" : "dasdasd"
                           },
                           "nolist" : [
                                                  
                           ],
                           "a1" : 1,
                           "d1" : 11231231231313133,
                           "g1" : 2,
                           "list" : [
                             "husdasdad",
                             "siudsan"
                           ],
                           "strs" : [
                             "231231",
                             "sdadsasdasd"
                           ],
                           "array2" : [
                             [
                               1,
                               2,
                               3,
                               4
                             ],
                             [
                               10,
                               12
                             ]
                           ],
                           "a" : 12,
                           "data" : [
                             {
                               "age" : 12,
                               "name" : "das"
                             },
                             {
                               "age" : 1222,
                               "name" : "daasdadasd"
                             }
                           ],
                           "b1" : 2.3399999999999999,
                           "array4" : [
                             [
                               1,
                               2,
                               3,
                               4,
                               5,
                               56
                             ],
                             [
                               10,
                               11,
                               12,
                               14
                             ]
                           ]
                         }
                         """;
        Dson.fromString(Data.class, content);
    }

    @Test
    public void test5()
    {
        String content = """
                         {"csss":"hhh"}
                         """;
        Dson.fromString(BaseData.class,content);
    }
}
