package com.jfireframework.dson;

import com.jfireframework.dson.metadata.EntryDeserializerFactory;
import com.jfireframework.dson.metadata.PropertySerializerFactory;
import com.jfireframework.dson.serializer.ArraySerializer;
import com.jfireframework.dson.serializer.BeanSerializer;
import com.jfireframework.dson.serializer.CollectionSerializer;
import com.jfireframework.dson.serializer.MapSerializer;

public interface JsonProcessorConfiguration
{
	Class<? extends PropertySerializerFactory> getPropertySerializerFactoryClass();
	
	Class<? extends EntryDeserializerFactory> getEntryDeserializerFactoryClass();
	
	Class<? extends MapSerializer> getMapSerializerClass();
	
	Class<? extends CollectionSerializer> getCollectionSerializerClass();
	
	Class<? extends BeanSerializer> getBeanSerializerClass();
	
	Class<? extends ArraySerializer> getArraySerializerClass();
	
}
