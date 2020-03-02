package com.jfirer.dson;

import com.jfirer.dson.reader.JsonReader;
import com.jfirer.dson.reader.Stream;
import com.jfirer.dson.reader.TypeReader;
import org.junit.Test;

public class ReaderTest
{
    public static class SimpleData
    {
        private int age;

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
        String     content    = "{\"age\":12,\"name\":\"lr\",\"sex\":0}";
        JsonReader jsonReader = new JsonReader();
        TypeReader typeReader = jsonReader.get(SimpleData.class);
        SimpleData instance   = (SimpleData) typeReader.fromString(new Stream(content));
        System.out.println(instance.getAge());
    }
}
