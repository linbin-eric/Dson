package com.jfireframework.dson.deserializer;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import com.jfireframework.baseutil.reflect.TypeUtil;

public class Demo
{
	public static class Person<T>
	{
		private T name;
	}
	
	public static void main(String[] args) throws SecurityException, NoSuchFieldException
	{
		Type type = new TypeUtil<Person<String>>() {}.getType();
		Class<?> ckass = (Class<?>) ((ParameterizedType) type).getRawType();
		System.out.println(ckass);
		Field field = ckass.getDeclaredField("name");
		System.out.println(field.getGenericType());
	}
}
