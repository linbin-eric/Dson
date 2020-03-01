package com.jfirer.dson.deserializer.buildin;

import java.lang.reflect.Type;
import java.util.Map;
import com.jfirer.dson.deserializer.DeserializeDescriptor;
import com.jfirer.dson.deserializer.Deserializer;
import com.jfirer.dson.metadata.json.DsonObject;
import com.jfirer.dson.metadata.json.Element;
import com.jfirer.dson.metadata.json.Entry;
import com.jfirer.dson.metadata.json.JsonValueType;

public class ByteDeserializeDescriptor implements DeserializeDescriptor
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
		return Byte.valueOf(json);
	}
	
	@Override
	public Object deserialize(Entry entry)
	{
		if (entry.getValueType() != JsonValueType.NUMBER_LONG)
		{
			throw new IllegalArgumentException();
		}
		return ((Long) entry.getValue()).byteValue();
	}
	
	@Override
	public Object deserialize(Element element)
	{
		if (element.getValueType() != JsonValueType.NUMBER_LONG)
		{
			throw new IllegalArgumentException();
		}
		return ((Long) element.getValue()).byteValue();
	}
	
}
