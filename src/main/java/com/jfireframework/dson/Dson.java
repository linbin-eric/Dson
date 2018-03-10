package com.jfireframework.dson;

import java.lang.reflect.Type;
import com.jfireframework.dson.deserializer.Deserializer;
import com.jfireframework.dson.deserializer.support.DefaultDeserializer;
import com.jfireframework.dson.serializer.Serializer;
import com.jfireframework.dson.serializer.support.CompileSerializer;
import com.jfireframework.dson.util.StringCacheAdaptStringOutput;
import com.jfireframework.dson.util.StringOutput;

public class Dson
{
	private static Serializer						serializer		= new CompileSerializer();
	private static Deserializer						deserializer	= new DefaultDeserializer();
	private static final ThreadLocal<StringOutput>	LOCAL			= new ThreadLocal<StringOutput>() {
																		protected StringOutput initialValue()
																		{
																			return new StringCacheAdaptStringOutput();
																		}
																	};
	
	public static String toJsonString(Object entity)
	{
		StringOutput output = LOCAL.get().clear();
		serializer.serialize(entity, output);
		return output.toString();
	}
	
	public static <T> T fromString(Type type, String json)
	{
		return deserializer.deserialize(type, json);
	}
}
