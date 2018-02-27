package com.jfireframework.dson.deserializer;

import com.jfireframework.dson.metadata.json.Entry;

public interface PropertyDeserializer
{
	String propertyName();
	
	void deserialize(Entry entry, Object bean);
}