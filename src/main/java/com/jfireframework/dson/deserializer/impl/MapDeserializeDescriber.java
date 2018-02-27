package com.jfireframework.dson.deserializer.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import com.jfireframework.dson.deserializer.DeserializeDescriber;
import com.jfireframework.dson.deserializer.Deserializer;
import com.jfireframework.dson.deserializer.token.Lexer;
import com.jfireframework.dson.metadata.json.DsonObject;

public class MapDeserializeDescriber implements DeserializeDescriber
{
	private DeserializeDescriber	keyDescriber;
	private DeserializeDescriber	valueDescriber;
	
	private @Override public void initialize(Type type, Deserializer deserializer)
	{
		
		if (type instanceof ParameterizedType)
		{
			
		}
		else if (type instanceof Class)
		{
			
		}
		else
		{
			throw new IllegalArgumentException();
		}
		if (Map.class.isAssignableFrom((Class<?>) type) == false)
		{
			throw new IllegalArgumentException();
		}
	}
	
	@Override
	public Object deserialize(DsonObject dsonObject)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Object deserialize(String json)
	{
		return deserialize(new Lexer(json).parse());
	}
	
}
