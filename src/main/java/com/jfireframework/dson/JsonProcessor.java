package com.jfireframework.dson;

import com.jfireframework.dson.metadata.PropertySerializerFactory;
import com.jfireframework.dson.serializer.BeanSerializer;
import com.jfireframework.dson.serializer.CollectionSerializer;
import com.jfireframework.dson.serializer.MapSerializer;

public interface JsonProcessor
{
    PropertySerializerFactory propertySerializerFactory();
    
    Class<MapSerializer<?>> mapSerializerClass();
    
    Class<CollectionSerializer<?>> collectionSerializerClass();
    
    Class<BeanSerializer<?>> beanSerializerClass();
    
    void serialize(Object entity, StringOutput output);
}
