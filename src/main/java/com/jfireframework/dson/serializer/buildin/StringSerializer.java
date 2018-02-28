package com.jfireframework.dson.serializer.buildin;

import com.jfireframework.dson.Serializer;
import com.jfireframework.dson.serializer.SerializeDescriptor;
import com.jfireframework.dson.util.StringOutput;

public class StringSerializer implements SerializeDescriptor
{
	
	@Override
	public void initialize(Serializer jsonProcessor, Class<?> type)
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
