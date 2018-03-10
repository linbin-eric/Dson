package com.jfireframework.dson;

import org.junit.Test;
import com.jfireframework.baseutil.time.NanoTimeWatch;
import com.jfireframework.codejson.JsonTool;
import com.jfireframework.dson.serializer.SerializeDescriptor;

public class SpeedTest
{
    public static class Person
    {
        private String  name;
        private Integer age;
        
        public String getName()
        {
            return name;
        }
        
        public void setName(String name)
        {
            this.name = name;
        }
        
        public Integer getAge()
        {
            return age;
        }
        
        public void setAge(Integer age)
        {
            this.age = age;
        }
        
    }
    
    @Test
    public void test()
    {
        Person person = new Person();
        person.setName("linbin");
        person.setAge(14);
        for (int i = 0; i < 100; i++)
        {
            Dson.toJsonString(person);
            JsonTool.write(person);
        }
        int count = 30000000;
        NanoTimeWatch nanoTimeWatch = new NanoTimeWatch();
        nanoTimeWatch.start();
        for (int i = 0; i < count; i++)
        {
            Dson.toJsonString(person);
        }
        nanoTimeWatch.end();
        long t0 = nanoTimeWatch.getTatol() / 1000000;
        nanoTimeWatch.start();
        for (int i = 0; i < count; i++)
        {
        	JsonTool.write(person);
        }
        nanoTimeWatch.end();
        long t1 = nanoTimeWatch.getTatol() / 1000000;
        System.out.println("Codejson:" + t1);
        System.out.println("    Dson:" + t0);
        int rate = (int) (((float) t0 / t1) * 100);
        System.out.println(rate);
    }
}
