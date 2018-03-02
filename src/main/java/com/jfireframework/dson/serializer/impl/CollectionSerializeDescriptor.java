package com.jfireframework.dson.serializer.impl;

import java.lang.reflect.Type;
import java.util.Collection;
import com.jfireframework.dson.serializer.SerializeDescriptor;
import com.jfireframework.dson.serializer.Serializer;
import com.jfireframework.dson.util.StringOutput;

public class CollectionSerializeDescriptor implements SerializeDescriptor
{
	private Serializer serializer;
	
	@Override
	public void initialize(Serializer serializer, Type type)
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
		boolean serialized = false;
		output.append('[');
		for (Object each : ((Collection<?>) entity))
		{
			if (each == null)
			{
				continue;
			}
			if (each instanceof Number)
			{
				output.append((Number) each).append(',');
				serialized = true;
			}
			else if (each instanceof Boolean)
			{
				output.append((Boolean) each).append(',');
				serialized = true;
			}
			else if (each instanceof String)
			{
				output.appendDoubleQuotes().append((String) each).append("\",");
				serialized = true;
			}
			else if (each instanceof Character)
			{
				output.appendDoubleQuotes().append((Character) each).append("\",");
				serialized = true;
			}
			else
			{
				int length = output.length();
				jsonProcessor.serialize(each, output);
				if (length != output.length())
				{
					output.append(',');
					serialized = true;
				}
			}
		}
		if (serialized)
		{
			output.deleteLast();
		}
		output.append(']');
		return true;
	}
	
	@Override
	public boolean serializeWithoutDoubleQuotes(Object entity, StringOutput output)
	{
		// TODO Auto-generated method stub
		return false;
	}
	
}
