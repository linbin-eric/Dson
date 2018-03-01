package com.jfireframework.dson.serializer;

import java.lang.reflect.Type;
import com.jfireframework.dson.util.StringOutput;

public interface SerializeDescriptor
{
	void initialize(Serializer serializer, Type type);
	
	/**
	 * 如果输出内容则返回true；反之返回false
	 * 
	 * @param entity
	 * @param output
	 * @return
	 */
	boolean serialize(Object entity, StringOutput output);
}
