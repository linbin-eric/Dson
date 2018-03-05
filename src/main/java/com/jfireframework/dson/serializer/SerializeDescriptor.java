package com.jfireframework.dson.serializer;

import java.lang.reflect.Type;
import java.util.Map;
import com.jfireframework.dson.util.StringOutput;

public interface SerializeDescriptor
{
	void initialize(Serializer serializer, Type type, Map<Type, SerializeDescriptor> map);
	
	/**
	 * 如果输出内容则返回true；反之返回false
	 * 
	 * @param entity
	 * @param output
	 * @return
	 */
	void serialize(Object entity, StringOutput output);
	
	void serializeWithoutDoubleQuotes(Object entity, StringOutput output);
}
