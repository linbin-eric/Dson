package com.jfireframework.dson;

import com.jfireframework.dson.metadata.PropertySerializerFactory;
import com.jfireframework.dson.serializer.ArraySerializer;
import com.jfireframework.dson.serializer.BeanSerializer;
import com.jfireframework.dson.serializer.CollectionSerializer;
import com.jfireframework.dson.serializer.MapSerializer;

public interface JsonProcessor
{
    void initialize(JsonProcessorConfiguration configuration);
    
    PropertySerializerFactory propertySerializerFactory();
    
    Class<MapSerializer> mapSerializerClass();
    
    Class<CollectionSerializer> collectionSerializerClass();
    
    Class<BeanSerializer> beanSerializerClass();
    
    Class<ArraySerializer> arraySerializerClass();
    
    void serialize(Object entity, StringOutput output);
}
