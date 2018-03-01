package com.jfireframework.dson.serializer.buildin;

import com.jfireframework.dson.serializer.SerializeDescriptor;
import com.jfireframework.dson.serializer.Serializer;
import com.jfireframework.dson.util.StringOutput;

public class NumberSerializer implements SerializeDescriptor
{
	
	@Override
	public void initialize(Serializer jsonProcessor, Class<?> type)
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
		output.append((Number) entity);
		return true;
	}
	
}
