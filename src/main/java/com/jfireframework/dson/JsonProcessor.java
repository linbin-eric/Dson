package com.jfireframework.dson;

import com.jfireframework.dson.serializer.ArraySerializer;
import com.jfireframework.dson.serializer.BeanSerializer;
import com.jfireframework.dson.serializer.CollectionSerializer;
import com.jfireframework.dson.serializer.MapSerializer;
import com.jfireframework.dson.serializer.property.PropertySerializerFactory;
import com.jfireframework.dson.util.StringOutput;

public interface JsonProcessor
{
	void initialize(JsonProcessorConfiguration configuration);
	
	PropertySerializerFactory propertySerializerFactory();
	
	Class<? extends MapSerializer> mapSerializerClass();
	
	Class<? extends CollectionSerializer> collectionSerializerClass();
	
	Class<? extends BeanSerializer> beanSerializerClass();
	
	Class<? extends ArraySerializer> arraySerializerClass();
	
	void serialize(Object entity, StringOutput output);
}
