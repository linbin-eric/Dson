package com.jfireframework.dson.metadata;

import com.jfireframework.dson.deserializer.Deserializer;
import com.jfireframework.dson.deserializer.PropertyDeserializer;

public interface EntryDeserializerFactory
{
	PropertyDeserializer[] get(Class<?> type, Deserializer deserializer);
}
