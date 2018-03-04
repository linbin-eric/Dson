package com.jfireframework.dson;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class Demo
{
	public static void main(String[] args) throws SecurityException, NoSuchFieldException
	{
		Field declaredField = Data.class.getDeclaredField("lists");
		Type genericType = declaredField.getGenericType();
		System.out.println(genericType);
	}
	
}
