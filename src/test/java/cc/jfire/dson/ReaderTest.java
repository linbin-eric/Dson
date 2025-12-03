package cc.jfire.dson;

import cc.jfire.dson.reader.Stream;
import cc.jfire.dson.reader.TypeReader;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReaderTest
{
    public static class SimpleData
    {
        private int                     age;
        private int[]                   nums;
        private int[][]                 twoDims;
        private List<String>            list;
        private Map<String, String>     map;
        private Map<String, SimpleData> innerMap;
        private String                  name;
        private Object                  unknowType;

        public Object getUnknowType()
        {
            return unknowType;
        }

        public void setUnknowType(Object unknowType)
        {
            this.unknowType = unknowType;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public Map<String, SimpleData> getInnerMap()
        {
            return innerMap;
        }

        public void setInnerMap(Map<String, SimpleData> innerMap)
        {
            this.innerMap = innerMap;
        }

        public Map<String, String> getMap()
        {
            return map;
        }

        public void setMap(Map<String, String> map)
        {
            this.map = map;
        }

        public List<String> getList()
        {
            return list;
        }

        public void setList(List<String> list)
        {
            this.list = list;
        }

        public int[][] getTwoDims()
        {
            return twoDims;
        }

        public void setTwoDims(int[][] twoDims)
        {
            this.twoDims = twoDims;
        }

        public int[] getNums()
        {
            return nums;
        }

        public void setNums(int[] nums)
        {
            this.nums = nums;
        }

        public int getAge()
        {
            return age;
        }

        public void setAge(int age)
        {
            this.age = age;
        }
    }

    @Test
    public void test()
    {
        SimpleData data = new SimpleData();
        data.setAge(12);
        data.setNums(new int[]{1, 2, 3, 4});
        data.setTwoDims(new int[][]{{1, 2,}, {3, 4}});
        Map<String, String> map = new HashMap<String, String>();
        map.put("abc", "anc");
        data.setMap(map);
        Map<String, SimpleData> innerMap = new HashMap<String, SimpleData>();
        SimpleData              data2    = new SimpleData();
        data2.setAge(12);
        data2.setName("data2");
        innerMap.put("fir", data2);
        data.setInnerMap(innerMap);
        List<String> list = new ArrayList<String>();
        list.add("12");
        list.add("34");
        data.setList(list);
        Map map2 = new HashMap();
        map2.put("121", 12);
        data.setUnknowType(map2);
        String s = Dson.toJson(data);
        System.out.println(s);
        DsonContext jsonReader = new DsonContext();
        TypeReader  typeReader = jsonReader.parseReader(SimpleData.class);
        Object      o          = typeReader.fromString(new Stream(s));
        System.out.println(Dson.toJson(o));
        Assert.assertEquals(s, Dson.toJson(o));
    }
}
