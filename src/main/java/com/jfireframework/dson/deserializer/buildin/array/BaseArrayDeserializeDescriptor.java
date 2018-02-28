package com.jfireframework.dson.deserializer.buildin.array;

import java.lang.reflect.Type;
import com.jfireframework.dson.deserializer.DeserializeDescriptor;
import com.jfireframework.dson.deserializer.Deserializer;
import com.jfireframework.dson.metadata.json.Element;
import com.jfireframework.dson.metadata.json.Entry;
import com.jfireframework.dson.metadata.json.JsonArray;
import com.jfireframework.dson.metadata.json.JsonValueType;
import com.jfireframework.dson.metadata.parse.Lexer;

public abstract class BaseArrayDeserializeDescriptor implements DeserializeDescriptor
{
	@Override
	public void initialize(Type type, Deserializer deserializer)
	{
		if (type instanceof Class<?> == false)
		{
			throw new IllegalArgumentException();
		}
		Class<?> arrayType = (Class<?>) type;
		checkArrayType(arrayType);
	}
	
	protected abstract void checkArrayType(Class<?> arrayType);
	
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
