package com.jfireframework.dson.serializer;

import com.jfireframework.dson.Serializer;
import com.jfireframework.dson.util.StringOutput;

public interface SerializeDescriptor
{
	void initialize(Serializer jsonProcessor, Class<?> type);
	
	/**
	 * 如果输出内容则返回true；反之返回false
	 * 
	 * @param entity
	 * @param output
	 * @return
	 */
	boolean serialize(Object entity, StringOutput output);
}
