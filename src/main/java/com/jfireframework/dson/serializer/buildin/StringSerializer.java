package com.jfireframework.dson.serializer.buildin;

import com.jfireframework.dson.JsonProcessor;
import com.jfireframework.dson.StringOutput;
import com.jfireframework.dson.serializer.Serializer;

public class StringSerializer implements Serializer
{
	
	@Override
	public void initialize(JsonProcessor jsonProcessor, Class<?> type)
	{
		
	}
	
	@Override
	public boolean serialize(Object entity, StringOutput output)
	{
		if (entity == null)
		{
			return false;
		}
		output.appendDoubleQuotes().append((String) entity).appendDoubleQuotes();
		return true;
	}
	
}
