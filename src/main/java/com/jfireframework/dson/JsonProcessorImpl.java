package com.jfireframework.dson;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.dson.metadata.PropertySerializerFactory;
import com.jfireframework.dson.serializer.ArraySerializer;
import com.jfireframework.dson.serializer.BeanSerializer;
import com.jfireframework.dson.serializer.CollectionSerializer;
import com.jfireframework.dson.serializer.MapSerializer;
import com.jfireframework.dson.serializer.Serializer;

public class JsonProcessorImpl implements JsonProcessor
{
    private PropertySerializerFactory           propertySerializerFactory;
    private Class<MapSerializer>                mapSerializerClass;
    private Class<CollectionSerializer>         collectionSerializerClass;
    private Class<BeanSerializer>               beanSerializerClass;
    private Class<ArraySerializer>              arraySerializerClass;
    private ConcurrentMap<Class<?>, Serializer> store = new ConcurrentHashMap<Class<?>, Serializer>();
    
    @Override
    public PropertySerializerFactory propertySerializerFactory()
    {
        return propertySerializerFactory;
    }
    
    @Override
    public Class<MapSerializer> mapSerializerClass()
    {
        return mapSerializerClass;
    }
    
    @Override
    public Class<CollectionSerializer> collectionSerializerClass()
    {
        return collectionSerializerClass;
    }
    
    @Override
    public Class<BeanSerializer> beanSerializerClass()
    {
        return beanSerializerClass;
    }
    
    @Override
    public Class<ArraySerializer> arraySerializerClass()
    {
        return arraySerializerClass;
    }
    
    @Override
    public void serialize(Object entity, StringOutput output)
    {
        Class<?> type = entity.getClass();
        Serializer serializer = store.get(type);
        if (serializer == null)
        {
            if (Map.class.isAssignableFrom(type))
            {
                
            }
            else if (Collection.class.isAssignableFrom(type))
            {
                
            }
            else if (type.isArray())
            {
                
            }
            else
            {
                try
                {
                    BeanSerializer beanSerializer = beanSerializerClass.newInstance();
                    beanSerializer.initialize(this, type);
                    serializer = (serializer = store.putIfAbsent(type, beanSerializer)) == null ? beanSerializer : serializer;
                }
                catch (Throwable e)
                {
                    throw new JustThrowException(e);
                }
            }
        }
        serializer.serialize(entity, output);
    }
    
    @Override
    public void initialize(JsonProcessorConfiguration configuration)
    {
        beanSerializerClass = configuration.getBeanSerializerClass();
        mapSerializerClass = configuration.getMapSerializerClass();
        collectionSerializerClass = configuration.getCollectionSerializerClass();
        propertySerializerFactory = configuration.getPropertySerializerFactory();
        arraySerializerClass = configuration.getArraySerializerClass();
    }
    
}
