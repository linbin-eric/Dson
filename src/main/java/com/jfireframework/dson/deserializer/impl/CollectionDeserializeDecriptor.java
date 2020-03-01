package com.jfireframework.dson.deserializer.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.Map;

import com.jfireframework.dson.deserializer.DeserializeDescriptor;
import com.jfireframework.dson.deserializer.Deserializer;
import com.jfireframework.dson.metadata.json.DsonObject;
import com.jfireframework.dson.metadata.json.Element;
import com.jfireframework.dson.metadata.json.Entry;
import com.jfireframework.dson.metadata.json.JsonArray;
import com.jfireframework.dson.metadata.json.JsonValueType;
import com.jfireframework.dson.metadata.parse.Lexer;
import com.jfirer.baseutil.reflect.ReflectUtil;

public class CollectionDeserializeDecriptor implements DeserializeDescriptor
{
	private DeserializeDescriptor	elementDescriber;
	private Class<Collection<?>>	instanceType;
	
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(Type type, Deserializer deserializer, Map<Type, DeserializeDescriptor> map)
	{
		if (type instanceof ParameterizedType)
		{
			Type elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
			if (elementType instanceof WildcardType)
			{
				elementDescriber = deserializer.describe(Object.class, map);
			}
			else if (elementType instanceof Class<?> || elementType instanceof ParameterizedType)
			{
				elementDescriber = deserializer.describe(elementType, map);
			}
			else
			{
				throw new UnsupportedOperationException();
			}
			instanceType = (Class<Collection<?>>) ((ParameterizedType) type).getRawType();
		}
		else if (type instanceof Class<?>)
		{
			elementDescriber = deserializer.describe(Object.class, map);
			instanceType = (Class<Collection<?>>) type;
		}
		else
		{
			throw new IllegalArgumentException();
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object deserialize(DsonObject dsonObject)
	{
		JsonArray jsonArray = (JsonArray) dsonObject;
		try
		{
			Collection<? super Object> collection = (Collection<? super Object>) instanceType.newInstance();
			for (Element element : jsonArray.getElements())
			{
				if (element.getValueType() == JsonValueType.NULL)
				{
					continue;
				}
				collection.add(elementDescriber.deserialize(element));
			}
			return collection;
		}
		catch (Exception e)
		{
			ReflectUtil.throwException(e);
			return null;
		}
	}
	
	@Override
	public Object deserialize(String json)
	{
		return deserialize(new Lexer(json).parse());
	}
	
	@Override
	public Object deserialize(Entry entry)
	{
		if (entry.getValueType() != JsonValueType.ARRAY)
		{
			throw new IllegalArgumentException();
		}
		return deserialize((JsonArray) entry.getValue());
	}
	
	@Override
	public Object deserialize(Element element)
	{
		if (element.getValueType() != JsonValueType.ARRAY)
		{
			throw new IllegalArgumentException();
		}
		return deserialize((JsonArray) element.getValue());
	}
	
}
