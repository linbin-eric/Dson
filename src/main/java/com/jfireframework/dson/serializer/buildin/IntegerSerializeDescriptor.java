package com.jfireframework.dson.serializer.buildin;

import java.lang.reflect.Type;
import com.jfireframework.dson.serializer.SerializeDescriptor;
import com.jfireframework.dson.serializer.Serializer;
import com.jfireframework.dson.util.StringOutput;

public class IntegerSerializeDescriptor implements SerializeDescriptor
{
	
	@Override
	public void initialize(Serializer serializer, Type type)
	{
		
	}
	
	@Override
	public boolean serialize(Object entity, StringOutput output)
	{
		if (entity == null)
		{
			return false;
		}
		output.append((Integer) entity);
		return true;
	}
	
	@Override
	public boolean serializeWithoutDoubleQuotes(Object entity, StringOutput output)
	{
		return serialize(entity, output);
	}
	
}
