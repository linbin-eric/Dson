package com.jfirer.dson.deserializer.buildin;

import java.lang.reflect.Type;
import java.util.Map;
import com.jfirer.dson.deserializer.DeserializeDescriptor;
import com.jfirer.dson.deserializer.Deserializer;
import com.jfirer.dson.metadata.json.DsonObject;
import com.jfirer.dson.metadata.json.Element;
import com.jfirer.dson.metadata.json.Entry;

public class ObjectDeserializeDescriptor implements DeserializeDescriptor
{
	
	@Override
	public void initialize(Type type, Deserializer deserializer, Map<Type, DeserializeDescriptor> map)
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
		return entry.getValue();
	}
	
	@Override
	public Object deserialize(Element element)
	{
		return element.getValue();
	}
	
}
