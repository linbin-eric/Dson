package com.jfirer.dson.util;

public class Person
{
    private String name;

    public void say()
    {
        System.out.println(GetFieldType.get(Person.class,"name"));
    }

    public static void main(String[] args)
    {
        new Person().say();
    }
}
