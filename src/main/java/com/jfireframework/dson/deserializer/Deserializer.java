package com.jfireframework.dson.deserializer;

import com.jfireframework.dson.metadata.EntryDeserializerFactory;

public interface Deserializer
{
	EntryDeserializerFactory entryDeserializerFactory();
	
	DeserializeDescriber describe(Class<?> type);
}
