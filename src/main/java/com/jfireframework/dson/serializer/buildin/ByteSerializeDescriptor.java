package com.jfireframework.dson.serializer.buildin;

import java.lang.reflect.Type;
import com.jfireframework.dson.serializer.SerializeDescriptor;
import com.jfireframework.dson.serializer.Serializer;
import com.jfireframework.dson.util.StringOutput;

public class ByteSerializeDescriptor implements SerializeDescriptor
{
	@Override
	public boolean serialize(Object entity, StringOutput output)
	{
		if (entity == null)
		{
			return false;
		}
		output.append((Byte) entity);
		return true;
	}
	
	@Override
	public boolean serializeWithoutDoubleQuotes(Object entity, StringOutput output)
	{
		return serialize(entity, output);
	}
	
	@Override
	public void initialize(Serializer serializer, Type type)
	{
		// TODO Auto-generated method stub
		
	}
	
}
