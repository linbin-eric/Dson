package com.jfirer.dson;

import com.alibaba.fastjson.JSON;
import com.jfirer.baseutil.reflect.TypeUtil;
import com.jfirer.dson.model.*;
import com.jfirer.dson.util.JsonRename;
import com.jfirer.dson.util.impl.HumpToUnderline;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class FunctionTest extends Support
{
    private DsonContext dsonContext;

    public FunctionTest(DsonConfig dsonConfig)
    {
        dsonContext = new DsonContext(dsonConfig);
    }

    @Parameterized.Parameters
    public static Collection<DsonConfig> data()
    {
        List<DsonConfig> list = new LinkedList<>();
        list.add(DsonConfig.STANDARD);
        list.add(new DsonConfig().setReadUseCompile(true));
        list.add(new DsonConfig().setReadEntryUseCompile(true));
        list.add(new DsonConfig().setValueAccessorUseCompile(true));
        list.add(new DsonConfig().setWriteUseCompile(true));
        return list;
    }

    @Test
    public void rightTest()
    {
        String string = dsonContext.toJson(data);
        logger.debug("输出的json是\n\n{}\r\n\n", string);
        System.out.println(dsonContext.toJson(dsonContext.fromString(Data.class, string)));
        assertTrue(data.equal(dsonContext.fromString(Data.class, string)));
        logger.debug("输出的数组json是\n\n{}\r\n\n", dsonContext.toJson(new Data[]{data, data}));
        Data[][] origin = new Data[][]{{data, data}, {data, data, data}};
        string = dsonContext.toJson(origin);
        Data[][] result = dsonContext.fromString(Data[][].class, string);
        for (int i = 0; i < origin.length; i++)
        {
            for (int j = 0; j < origin[i].length; j++)
            {
                assertTrue(origin[i][j].equal(result[i][j]));
            }
        }
        Data[] test1 = new Data[]{data, data};
        string = dsonContext.toJson(test1);
        Data[] test1Result = dsonContext.fromString(Data[].class, string);
        for (int i = 0; i < test1.length; i++)
        {
            assertTrue(test1[i].equal(test1Result[i]));
        }
        Data[][][] test2 = new Data[][][]{{{data, data}, {data}}, {{data, data, data, data}, {data}, {data}}};
        string = dsonContext.toJson(test2);
        Data[][][] test2Result = dsonContext.fromString(Data[][][].class, string);
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
        String value = dsonContext.toJson(list);
        ArrayList<String> result = (ArrayList<String>) dsonContext.fromString(new TypeUtil<ArrayList<String>>()
        {
        }.getType(), value);
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
        value = dsonContext.toJson(arrayList);
        System.out.println(value);
        ArrayList<NestData> result1 = dsonContext.fromString(new TypeUtil<ArrayList<NestData>>()
        {
        }.getType(), value);
        assertTrue(arrayList.equals(result1));
        ArrayList<Data> list2 = new ArrayList<Data>();
        list2.add(data);
        list2.add(data);
        value = dsonContext.toJson(list2);
        System.out.println(value);
        ArrayList<Data> result3 = dsonContext.fromString(new TypeUtil<ArrayList<Data>>()
        {
        }.getType(), value);
        assertTrue(list2.get(0).equal(result3.get(0)) && list2.get(1).equal(result3.get(1)));
        HashMap<String, Data> map = new HashMap<String, Data>();
        map.put("12wq", data);
        map.put("xczc", data);
        value = dsonContext.toJson(map);
        HashMap<String, Data> result4 = dsonContext.fromString(new TypeUtil<HashMap<String, Data>>()
        {
        }.getType(), value);
        assertTrue(map.get("12wq").equal(result4.get("12wq")));
        assertTrue(map.get("xczc").equal(result4.get("xczc")));
    }

    @Test
    public void test1()
    {
        assertEquals("{\"data\":{\"1\":\"121212\"}}", dsonContext.toJson(new FunctionData7()));
    }

    @Test
    public void test2()
    {
        System.out.println(dsonContext.toJson(new FunctionData10()));
    }

    @Test
    public void test3()
    {
        String json = dsonContext.toJson(new FunctionData13());
        System.out.println(json);
        assertTrue(json.contains("""
                                         "array1":["1212","12112"]"""));
        assertTrue(json.contains("""
                                         "array2":[1,2,3]"""));
        assertTrue(json.contains("""
                                         "array6":[1221121231231,212312313]"""));
    }

    @Test
    public void enumTest()
    {
        FunData16 data16 = new FunData16();
        data16.setTest(TestEnum.PUSH);
        assertEquals("{\"test\":\"PUSH\"}", dsonContext.toJson(data16));
    }

    @Test
    public void definitionTest()
    {
        FunctionData16 data16 = new FunctionData16();
        data16.setName("111");
        assertEquals("{\"name\":\"123\"}", dsonContext.toJson(data16));
    }

    @Test
    public void readStringTest()
    {
        FunctionData17 data17   = new FunctionData17();
        BaseData       baseData = new BaseData();
        data17.setValue(baseData);
        String         json  = dsonContext.toJson(data17);
        FunctionData17 o     = dsonContext.fromString(FunctionData17.class, json);
        String         value = (String) o.getValue();
        assertEquals(value, dsonContext.toJson(baseData));
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
        dsonContext.fromString(Data.class, content);
        List<String> list = (List<String>) dsonContext.fromStringByAttribute("list", new TypeUtil<ArrayList<String>>()
        {
        }.getType(), content);
        assertEquals(2, list.size());
        assertEquals("siudsan", list.get(1));
    }

    @Test
    public void test5()
    {
        String content = """
                {
                "csss":"hhh",
                  "testNull":null,
                  "testNullList":["abc",null,"cde"]
                }
                """;
        BaseData data1 = dsonContext.fromString(BaseData.class, content);
        assertNull(data1.getTestNull());
        assertEquals(2, data1.getTestNullList().size());
        assertEquals("abc", data1.getTestNullList().get(0));
        assertEquals("cde", data1.getTestNullList().get(1));
    }

    @JsonRename(strategy = HumpToUnderline.class)
    @lombok.Data
    public static class ReNameTest
    {
        private String myName;
    }

    @Test
    public void test6()
    {
        String content = """
                {"my_name":"lin"}""";
        ReNameTest reNameTest = new ReNameTest();
        reNameTest.setMyName("lin");
        assertEquals(content, dsonContext.toJson(reNameTest));
        ReNameTest o = dsonContext.fromString(ReNameTest.class, content);
        assertEquals("lin", o.getMyName());
    }

    @Test
    public void test7()
    {
        FunctionData data = new FunctionData();
        data.setA(12);
        data.setB(2.36f);
        data.setC(5.6987);
        data.setD(121212121212l);
        data.setE('f');
        data.setF(true);
        data.setG((short) 5.689);
        data.setH((byte) 3);
        data.setA1(1);
        data.setB1(2.34f);
        data.setC1(2323.34234234);
        data.setD1(11231231231313133l);
        data.setE1("sdasdasd");
        data.setF1(true);
        data.setG1((short) 2);
        data.setH1((byte) 12);
        NestData nestData = new NestData();
        nestData.setName("dsadas");
        data.setNestData(nestData);
        ArrayList<String> list = new ArrayList<String>();
        list.add("husdasdad");
        list.add("siudsan");
        data.setList(list);
        ArrayList<NestData> nestDatas = new ArrayList<NestData>();
        nestData = new NestData();
        nestData.setName("sdasda");
        nestData.setAge(13);
        nestDatas.add(nestData);
        nestData = new NestData();
        nestData.setName("dasdas");
        nestData.setAge(20);
        nestDatas.add(nestData);
        data.setDatas(nestDatas);
        data.setNolist(new ArrayList<String>());
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("恁大", "dasdasd");
        map.put("dsada", "你好");
        data.setMap(map);
        data.setArray2(new int[][]{{1, 2, 3, 4}, {10, 12}});
        data.setStrs(new String[]{"231231", "sdadsasdasd"});
        data.setArray1(new int[]{1, 2, 3, 4, 5, 65});
        data.setChars(new char[]{'a', 'b'});
        data.setArray3(new Integer[]{1, 2, 3, 4, 5, 6, 7});
        data.setArray4(new Integer[][]{{1, 2, 3, 4, 5, 56}, {10, 11, 12, 14}});
        NestData[] nestDatas2 = new NestData[2];
        NestData   tmp        = new NestData();
        tmp.setAge(12);
        tmp.setName("das");
        nestDatas2[0] = tmp;
                        tmp = new NestData();
        tmp.setAge(1222);
        tmp.setName("daasdadasd");
        nestDatas2[1] = tmp;
        data.setNestDatas(nestDatas2);
        ArrayList<String>[] lists = new ArrayList[]{new ArrayList<String>(), new ArrayList<String>()};
        lists[0].add("dasdasda");
        lists[0].add("dasdasdasdasdasd");
        lists[1].add("1212121dasdasdasdasdasd");
        lists[1].add("dasdasd1212121212asdasdasd");
        data.setLists(lists);
        data.setData(nestDatas2);
        String       content = dsonContext.toJson(data);
        FunctionData result  = dsonContext.fromString(FunctionData.class, content);
        System.out.println(result.getB1());
        assertTrue(data.equal(result));
        String string = dsonContext.toJson(data);
        logger.debug("输出的json是\n\n{}\r\n\n", string);
        assertTrue(data.equal(dsonContext.fromString(FunctionData.class, string)));
    }

    @lombok.Data
    public static class FinalDataTest
    {
        private final String d = "ssss";
    }

    @Test
    public void test8()
    {
        String json = Dson.toJson(new FinalDataTest());
        assertEquals("""
                             {"d":"ssss"}""", json);
    }

    @lombok.Data
    public static class NullString
    {
        private String a;
    }

    @Test
    public void test9()
    {
        System.out.println(Dson.toJson(new NullString()));
    }

    @Test
    public void test10()
    {
        List<String> l = new ArrayList<>();
        l.add("""
                      a"s"s""");
        String s = JSON.toJSONString(l);
        System.out.println(s);
        System.out.println("[\"a\\\"ss\"]");
    }
}
