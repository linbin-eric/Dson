package com.jfireframework.dson.metadata;

import com.jfireframework.dson.serializer.PropertySerializer;
import com.jfireframework.dson.serializer.Serializer;

public interface PropertySerializerFactory
{
	void initialize(Serializer jsonProcessor);
	
	PropertySerializer get(Class<?> type, String property);
}
