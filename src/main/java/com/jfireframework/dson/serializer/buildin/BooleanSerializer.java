package com.jfireframework.dson.serializer.buildin;

import com.jfireframework.dson.JsonProcessor;
import com.jfireframework.dson.serializer.Serializer;
import com.jfireframework.dson.util.StringOutput;

public class BooleanSerializer implements Serializer
{
	
	@Override
	public void initialize(JsonProcessor jsonProcessor, Class<?> type)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean serialize(Object entity, StringOutput output)
	{
		if (entity == null)
		{
			return false;
		}
		output.append((Boolean) entity);
		return true;
	}
	
}
