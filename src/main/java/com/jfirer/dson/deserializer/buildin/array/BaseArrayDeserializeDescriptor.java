package com.jfirer.dson.deserializer.buildin.array;

import java.lang.reflect.Type;
import java.util.Map;
import com.jfirer.dson.deserializer.DeserializeDescriptor;
import com.jfirer.dson.deserializer.Deserializer;
import com.jfirer.dson.metadata.json.Element;
import com.jfirer.dson.metadata.json.Entry;
import com.jfirer.dson.metadata.json.JsonArray;
import com.jfirer.dson.metadata.json.JsonValueType;
import com.jfirer.dson.metadata.parse.Lexer;

public abstract class BaseArrayDeserializeDescriptor implements DeserializeDescriptor
{
	@Override
	public void initialize(Type type, Deserializer deserializer, Map<Type, DeserializeDescriptor> map)
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
