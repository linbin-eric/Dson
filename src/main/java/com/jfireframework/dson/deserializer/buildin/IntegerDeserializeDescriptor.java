package com.jfireframework.dson.deserializer.buildin;

import java.lang.reflect.Type;
import java.util.Map;
import com.jfireframework.dson.deserializer.DeserializeDescriptor;
import com.jfireframework.dson.deserializer.Deserializer;
import com.jfireframework.dson.metadata.json.DsonObject;
import com.jfireframework.dson.metadata.json.Element;
import com.jfireframework.dson.metadata.json.Entry;
import com.jfireframework.dson.metadata.json.JsonValueType;

public class IntegerDeserializeDescriptor implements DeserializeDescriptor
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
		return Integer.valueOf(json);
	}
	
	@Override
	public Object deserialize(Entry entry)
	{
		if (entry.getValueType() != JsonValueType.NUMBER_LONG)
		{
			return null;
		}
		return ((Long) entry.getValue()).intValue();
	}
	
	@Override
	public Object deserialize(Element element)
	{
		if (element.getValueType() != JsonValueType.NUMBER_LONG)
		{
			return null;
		}
		return ((Long) element.getValue()).intValue();
	}
	
}
