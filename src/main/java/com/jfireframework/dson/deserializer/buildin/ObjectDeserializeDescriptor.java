package com.jfireframework.dson.deserializer.buildin;

import java.lang.reflect.Type;
import com.jfireframework.dson.deserializer.DeserializeDescriptor;
import com.jfireframework.dson.deserializer.Deserializer;
import com.jfireframework.dson.metadata.json.DsonObject;
import com.jfireframework.dson.metadata.json.Element;
import com.jfireframework.dson.metadata.json.Entry;

public class ObjectDeserializeDescriptor implements DeserializeDescriptor
{
	
	@Override
	public void initialize(Type type, Deserializer deserializer)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Object deserialize(DsonObject dsonObject)
	{
		return dsonObject;
	}
	
	@Override
	public Object deserialize(String json)
	{
		return json;
	}
	
	@Override
	public Object deserialize(Entry entry)
	{
		return entry;
	}
	
	@Override
	public Object deserialize(Element element)
	{
		return element;
	}
	
}
