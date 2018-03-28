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
		String value = (String) entity;
		value = value.replace("\"", "\\\"");
		output.appendDoubleQuotes().append(value).appendDoubleQuotes();
	}
	
	@Override
	public void serializeWithoutDoubleQuotes(Object entity, StringOutput output)
	{
		if (entity == null)
		{
			return;
		}
		String value = (String) entity;
		value = value.replace("\"", "\\\"");
		output.append(value);
	}
	
	@Override
	public void initialize(Serializer serializer, Type type, Map<Type, SerializeDescriptor> map)
	{
		// TODO Auto-generated method stub
		
	}
	
}
