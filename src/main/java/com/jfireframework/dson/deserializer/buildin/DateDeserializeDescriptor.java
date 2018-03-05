package com.jfireframework.dson.deserializer.buildin;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;
import com.jfireframework.dson.deserializer.DeserializeDescriptor;
import com.jfireframework.dson.deserializer.Deserializer;
import com.jfireframework.dson.metadata.json.DsonObject;
import com.jfireframework.dson.metadata.json.Element;
import com.jfireframework.dson.metadata.json.Entry;
import com.jfireframework.dson.metadata.json.JsonValueType;

public class DateDeserializeDescriptor implements DeserializeDescriptor
{
	
	@Override
	public void initialize(Type type, Deserializer deserializer, Map<Type, DeserializeDescriptor> map)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Object deserialize(DsonObject dsonObject)
	{
		throw new IllegalArgumentException();
	}
	
	@Override
	public Object deserialize(String json)
	{
		throw new IllegalArgumentException();
	}
	
	@Override
	public Object deserialize(Entry entry)
	{
		if (entry.getValueType() != JsonValueType.NUMBER_LONG)
		{
			throw new IllegalArgumentException();
		}
		return new Date((Long) entry.getValue());
	}
	
	@Override
	public Object deserialize(Element element)
	{
		if (element.getValueType() != JsonValueType.NUMBER_LONG)
		{
			throw new IllegalArgumentException();
		}
		return new Date((Long) element.getValue());
	}
	
}
