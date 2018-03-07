package com.jfireframework.dson;

import java.util.Date;
import org.junit.Test;

public class DateTest
{
	public static class Person
	{
		Date date;
	}
	
	@Test
	public void test()
	{
		Person person = new Person();
		person.date = new Date();
		String value = Dson.toJsonString(person);
		System.out.println(value);
		person = Dson.fromString(Person.class, value);
		System.out.println(person.date);
	}
}
