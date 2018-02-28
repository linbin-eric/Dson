package com.jfireframework.dson.deserializer.buildin;

import java.lang.reflect.Type;
import com.jfireframework.dson.deserializer.DeserializeDescriptor;
import com.jfireframework.dson.deserializer.Deserializer;
import com.jfireframework.dson.metadata.json.DsonObject;
import com.jfireframework.dson.metadata.json.Element;
import com.jfireframework.dson.metadata.json.Entry;
import com.jfireframework.dson.metadata.json.JsonValueType;

public class BooleanDeserializeDescriptor implements DeserializeDescriptor
{
	
	@Override
	public void initialize(Type type, Deserializer deserializer)
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
		return Boolean.valueOf(json);
	}
	
	@Override
	public Object deserialize(Entry entry)
	{
		if (entry.getValueType() != JsonValueType.BOOLEAN)
		{
			throw new IllegalArgumentException();
		}
		return (Boolean) entry.getValue();
	}
	
	@Override
	public Object deserialize(Element element)
	{
		if (element.getValueType() != JsonValueType.BOOLEAN)
		{
			throw new IllegalArgumentException();
		}
		return (Boolean) element.getValue();
	}
	
}
