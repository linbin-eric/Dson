package com.jfirer.dson;

import lombok.Data;
import org.junit.Ignore;
import org.junit.Test;

public class CycTest
{
    @Data
    static final class A
    {
        int age;
        B   b;
    }

    @Data
    static final class B
    {
        int dd;
        A   a;
    }

    @Test
    @Ignore
    public void test()
    {
        A a = new A();
        B b = new B();
        a.setB(b);
        b.setA(a);
        System.out.println(Dson.toJson(a));
    }
}
