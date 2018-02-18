package com.jfireframework.dson;

import com.jfireframework.dson.metadata.PropertySerializerFactory;
import com.jfireframework.dson.serializer.ArraySerializer;
import com.jfireframework.dson.serializer.BeanSerializer;
import com.jfireframework.dson.serializer.CollectionSerializer;
import com.jfireframework.dson.serializer.MapSerializer;

public interface JsonProcessorConfiguration
{
    PropertySerializerFactory getPropertySerializerFactory();
    
    Class<MapSerializer> getMapSerializerClass();
    
    Class<CollectionSerializer> getCollectionSerializerClass();
    
    Class<BeanSerializer> getBeanSerializerClass();
    
    Class<ArraySerializer> getArraySerializerClass();
    
}
