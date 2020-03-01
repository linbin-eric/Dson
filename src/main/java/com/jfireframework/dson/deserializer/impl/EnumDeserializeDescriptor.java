package com.jfireframework.dson.deserializer.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import com.jfireframework.dson.deserializer.DeserializeDescriptor;
import com.jfireframework.dson.deserializer.Deserializer;
import com.jfireframework.dson.metadata.json.DsonObject;
import com.jfireframework.dson.metadata.json.Element;
import com.jfireframework.dson.metadata.json.Entry;
import com.jfireframework.dson.metadata.json.JsonValueType;
import com.jfirer.baseutil.reflect.ReflectUtil;

public class EnumDeserializeDescriptor implements DeserializeDescriptor
{
	private Map<String, ? extends Enum<?>> allEnumInstances;
	
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(Type type, Deserializer deserializer, Map<Type, DeserializeDescriptor> map)
	{
		if (type instanceof Class<?>)
		{
			allEnumInstances = ReflectUtil.getAllEnumInstances((Class<? extends Enum<?>>) type);
		}
		else if (type instanceof ParameterizedType)
		{
			allEnumInstances = ReflectUtil.getAllEnumInstances((Class<? extends Enum<?>>) ((ParameterizedType) type).getRawType());
		}
		else
		{
			throw new IllegalArgumentException("非法参数:" + type);
		}
	}
	
	@Override
	public Object deserialize(DsonObject dsonObject)
	{
		throw new IllegalArgumentException();
	}
	
	@Override
	public Object deserialize(String json)
	{
		return allEnumInstances.get(json);
	}
	
	@Override
	public Object deserialize(Entry entry)
	{
		if (entry.getValueType() != JsonValueType.STRING)
		{
			throw new IllegalArgumentException();
		}
		return allEnumInstances.get(entry.getValue());
	}
	
	@Override
	public Object deserialize(Element element)
	{
		if (element.getValueType() != JsonValueType.STRING)
		{
			throw new IllegalArgumentException();
		}
		return allEnumInstances.get(element.getValue());
	}
	
}
