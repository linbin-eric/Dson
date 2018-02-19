package com.jfireframework.dson.serializer.impl;

import java.util.Collection;
import com.jfireframework.dson.JsonProcessor;
import com.jfireframework.dson.StringOutput;
import com.jfireframework.dson.serializer.CollectionSerializer;

public class CollectionSerizlizerImpl implements CollectionSerializer
{
	private JsonProcessor jsonProcessor;
	
	@Override
	public void initialize(JsonProcessor jsonProcessor, Class<?> type)
	{
		this.jsonProcessor = jsonProcessor;
		
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
	
}
