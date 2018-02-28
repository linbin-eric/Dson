package com.jfireframework.dson.deserializer.buildin;

import java.lang.reflect.Type;
import com.jfireframework.dson.deserializer.DeserializeDescriptor;
import com.jfireframework.dson.deserializer.Deserializer;
import com.jfireframework.dson.metadata.json.DsonObject;
import com.jfireframework.dson.metadata.json.Element;
import com.jfireframework.dson.metadata.json.Entry;
import com.jfireframework.dson.metadata.json.JsonValueType;

public class StringDeserializDescriptor implements DeserializeDescriptor
{
	
	@Override
	public void initialize(Type type, Deserializer deserializer)
	{
		
	}
	
	@Override
	public Object deserialize(DsonObject dsonObject)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Object deserialize(String json)
	{
		return json;
	}
	
	@Override
	public Object deserialize(Entry entry)
	{
		if (entry.getValueType() != JsonValueType.STRING)
		{
			throw new IllegalArgumentException();
		}
		return (String) entry.getValue();
	}
	
	@Override
	public Object deserialize(Element element)
	{
		if (element.getValueType() != JsonValueType.STRING)
		{
			throw new IllegalArgumentException();
		}
		return (String) element.getValue();
	}
	
}
