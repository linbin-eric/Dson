package com.jfireframework.dson.serializer.impl;

import java.lang.reflect.Type;
import com.jfireframework.dson.serializer.SerializeDescriptor;
import com.jfireframework.dson.serializer.Serializer;
import com.jfireframework.dson.util.StringOutput;

public class EnumSerializeDescriptor implements SerializeDescriptor
{
	
	@Override
	public void initialize(Serializer serializer, Type type)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void serialize(Object entity, StringOutput output)
	{
		Enum<?> instance = (Enum<?>) entity;
		output.appendDoubleQuotes().append(instance.name()).appendDoubleQuotes();
	}
	
	@Override
	public void serializeWithoutDoubleQuotes(Object entity, StringOutput output)
	{
		Enum<?> instance = (Enum<?>) entity;
		output.append(instance.name());
	}
	
}
