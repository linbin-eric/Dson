package com.jfireframework.dson.serializer.buildin;

import java.lang.reflect.Type;
import java.util.Map;
import com.jfireframework.dson.serializer.SerializeDescriptor;
import com.jfireframework.dson.serializer.Serializer;
import com.jfireframework.dson.util.StringOutput;

public class StringSerializeDescriptor implements SerializeDescriptor
{
	@Override
	public void serialize(Object entity, StringOutput output)
	{
		if (entity == null)
		{
			return;
		}
		output.appendDoubleQuotes().append((String) entity).appendDoubleQuotes();
	}
	
	@Override
	public void serializeWithoutDoubleQuotes(Object entity, StringOutput output)
	{
		if (entity == null)
		{
			return;
		}
		output.append((String) entity);
	}
	
	@Override
	public void initialize(Serializer serializer, Type type, Map<Type, SerializeDescriptor> map)
	{
		// TODO Auto-generated method stub
		
	}
	
}
