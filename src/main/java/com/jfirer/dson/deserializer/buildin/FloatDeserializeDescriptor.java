package com.jfirer.dson.deserializer.buildin;

import java.lang.reflect.Type;
import java.util.Map;
import com.jfirer.dson.deserializer.DeserializeDescriptor;
import com.jfirer.dson.deserializer.Deserializer;
import com.jfirer.dson.metadata.json.DsonObject;
import com.jfirer.dson.metadata.json.Element;
import com.jfirer.dson.metadata.json.Entry;
import com.jfirer.dson.metadata.json.JsonValueType;

public class FloatDeserializeDescriptor implements DeserializeDescriptor
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
		return Float.valueOf(json);
	}
	
	@Override
	public Object deserialize(Entry entry)
	{
		if (entry.getValueType() != JsonValueType.NUMBER_DOUBLE)
		{
			throw new IllegalArgumentException();
		}
		return ((Double) entry.getValue()).floatValue();
	}
	
	@Override
	public Object deserialize(Element element)
	{
		if (element.getValueType() != JsonValueType.NUMBER_DOUBLE)
		{
			throw new IllegalArgumentException();
		}
		return ((Double) element.getValue()).floatValue();
	}
	
}
