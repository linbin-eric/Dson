package com.jfireframework.dson.deserializer.buildin;

import java.lang.reflect.Type;
import java.util.Map;
import com.jfireframework.dson.deserializer.DeserializeDescriptor;
import com.jfireframework.dson.deserializer.Deserializer;
import com.jfireframework.dson.metadata.json.DsonObject;
import com.jfireframework.dson.metadata.json.Element;
import com.jfireframework.dson.metadata.json.Entry;
import com.jfireframework.dson.metadata.json.JsonValueType;

public class CharacterDeserializeDescriptor implements DeserializeDescriptor
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
		return json.charAt(0);
	}
	
	@Override
	public Object deserialize(Entry entry)
	{
		if (entry.getValueType() != JsonValueType.STRING)
		{
			throw new IllegalArgumentException();
		}
		return ((String) entry.getValue()).charAt(0);
	}
	
	@Override
	public Object deserialize(Element element)
	{
		if (element.getValueType() != JsonValueType.STRING)
		{
			throw new IllegalArgumentException();
		}
		return ((String) element.getValue()).charAt(0);
	}
	
}
