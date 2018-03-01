package com.jfireframework.dson.serializer.impl;

import java.util.Map;
import java.util.Map.Entry;
import com.jfireframework.dson.serializer.MapSerializer;
import com.jfireframework.dson.serializer.Serializer;
import com.jfireframework.dson.util.StringOutput;

public class MapSerializerImpl implements MapSerializer
{
	private Serializer jsonProcessor;
	
	@Override
	public boolean serialize(Object entity, StringOutput output)
	{
		if (entity == null)
		{
			return false;
		}
		output.append('{');
		boolean serialized = false;
		for (Entry<?, ?> entry : ((Map<?, ?>) entity).entrySet())
		{
			if (serialize(entry, output))
			{
				serialized = true;
				output.append(',');
			}
		}
		if (serialized)
		{
			output.deleteLast();
		}
		output.append('}');
		return true;
	}
	
	@Override
	public void initialize(Serializer jsonProcessor, Class<?> type)
	{
		this.jsonProcessor = jsonProcessor;
	}
	
	@Override
	public boolean serialize(Entry<?, ?> entry, StringOutput output)
	{
		Object value = entry.getValue();
		if (value == null)
		{
			return false;
		}
		output.appendDoubleQuotes().append(entry.getKey()).append("\":");
		if (value instanceof String || value instanceof Character)
		{
			output.append('"').append(value).append('"');
		}
		else if (value instanceof Number //
		        || value instanceof Boolean)
		{
			output.append(value);
		}
		else
		{
			jsonProcessor.serialize(value, output);
		}
		return true;
	}
}
