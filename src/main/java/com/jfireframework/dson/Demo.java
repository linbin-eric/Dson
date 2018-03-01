package com.jfireframework.dson;

import java.lang.reflect.Type;
import java.util.Map;
import com.jfireframework.baseutil.reflect.TypeUtil;

public class Demo
{
	public static void main(String[] args)
	{
		Type type = new TypeUtil<Map<String, String>>() {}.getType();
		Type type2 = new TypeUtil<Map<String, String>>() {}.getType();
		
		System.out.println(type.hashCode() == type2.hashCode());
		System.out.println(type.equals(type2));
		System.out.println(type);
		System.out.println(type2);
	}
}
