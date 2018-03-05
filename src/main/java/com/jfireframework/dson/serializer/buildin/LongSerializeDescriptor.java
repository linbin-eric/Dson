package com.jfireframework.dson.serializer.buildin;

import java.lang.reflect.Type;
import com.jfireframework.dson.serializer.SerializeDescriptor;
import com.jfireframework.dson.serializer.Serializer;
import com.jfireframework.dson.util.StringOutput;

public class LongSerializeDescriptor implements SerializeDescriptor
{
	
	@Override
	public void initialize(Serializer serializer, Type type)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void serialize(Object entity, StringOutput output)
	{
		if (entity == null)
		{
			return;
		}
		output.append((Long) entity);
	}
	
	@Override
	public void serializeWithoutDoubleQuotes(Object entity, StringOutput output)
	{
		serialize(entity, output);
	}
	
}
