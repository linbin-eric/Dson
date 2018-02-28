package com.jfireframework.dson.metadata;

import com.jfireframework.dson.Serializer;
import com.jfireframework.dson.serializer.PropertySerializer;

public interface PropertySerializerFactory
{
	void initialize(Serializer jsonProcessor);
	
	PropertySerializer get(Class<?> type, String property);
}
