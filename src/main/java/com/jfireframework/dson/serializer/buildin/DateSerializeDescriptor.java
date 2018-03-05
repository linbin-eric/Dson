package com.jfireframework.dson.serializer.buildin;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;
import com.jfireframework.dson.serializer.SerializeDescriptor;
import com.jfireframework.dson.serializer.Serializer;
import com.jfireframework.dson.util.StringOutput;

public class DateSerializeDescriptor implements SerializeDescriptor
{
	
	@Override
	public void initialize(Serializer serializer, Type type, Map<Type, SerializeDescriptor> map)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void serialize(Object entity, StringOutput output)
	{
		Date date = (Date) entity;
		output.append(date.getTime());
	}
	
	@Override
	public void serializeWithoutDoubleQuotes(Object entity, StringOutput output)
	{
		Date date = (Date) entity;
		output.append(date.getTime());
	}
	
}
