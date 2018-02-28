package com.jfireframework.dson;

import java.lang.reflect.Type;
import com.jfireframework.dson.serializer.SerializeDescriptor;
import com.jfireframework.dson.util.StringOutput;

public interface Serializer
{
	SerializeDescriptor describe(Type type);
	
	void serialize(Object entity, StringOutput output);
	
}
