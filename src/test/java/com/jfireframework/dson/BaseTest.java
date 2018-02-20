package com.jfireframework.dson;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class BaseTest
{
	class Person
	{
		private String	name;
		private Integer	age;
	}
	
	@Test
	public void test()
	{
		Person person = new Person();
		person.name = "linbin";
		person.age = 13;
		System.out.println(Dson.toJsonString(person));
	}
	
	@Test
	public void test_2()
	{
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("name", "linbin");
		data.put("age", 12);
		System.out.println(Dson.toJsonString(data));
	}
}
