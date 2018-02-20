package com.jfireframework.dson;

import com.jfireframework.dson.util.StringCacheAdaptStringOutput;
import com.jfireframework.dson.util.StringOutput;

public class Dson
{
	private static JsonProcessor defaultProcessor;
	static
	{
		defaultProcessor = new JsonProcessorImpl();
		defaultProcessor.initialize(new DefaultJsonProcessorConfiguration());
	}
	
	public static final JsonProcessor defaultProcessor()
	{
		return defaultProcessor;
	}
	
	private static final ThreadLocal<StringOutput> LOCAL = new ThreadLocal<StringOutput>() {
		protected StringOutput initialValue()
		{
			return new StringCacheAdaptStringOutput();
		}
	};
	
	public static String toJsonString(Object entity)
	{
		StringOutput output = LOCAL.get().clear();
		defaultProcessor.serialize(entity, output);
		return output.toString();
	}
}
