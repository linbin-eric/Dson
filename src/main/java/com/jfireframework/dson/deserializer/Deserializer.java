package com.jfireframework.dson.deserializer;

import java.lang.reflect.Type;

public interface Deserializer
{
	DeserializeDescriptor describe(Type type);
	
	<T> T deserialize(Type type, String json);
}
