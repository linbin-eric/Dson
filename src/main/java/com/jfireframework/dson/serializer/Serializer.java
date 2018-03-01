package com.jfireframework.dson.serializer;

import java.lang.reflect.Type;
import com.jfireframework.dson.util.StringOutput;

public interface Serializer
{
	SerializeDescriptor describe(Type type);
	
	void serialize(Object entity, StringOutput output);
	
	void serializeWithoutDoubleQuotes(Object entity, StringOutput output);
}
