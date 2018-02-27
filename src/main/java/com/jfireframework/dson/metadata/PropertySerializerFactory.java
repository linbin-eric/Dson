package com.jfireframework.dson.metadata;

import com.jfireframework.dson.JsonProcessor;
import com.jfireframework.dson.serializer.PropertySerializer;

public interface PropertySerializerFactory
{
	void initialize(JsonProcessor jsonProcessor);
	
	PropertySerializer get(Class<?> type, String property);
}
