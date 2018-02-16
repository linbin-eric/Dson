package com.jfireframework.dson.serializer;

import java.util.Set;

public interface MapSerializer<T> extends Serializer<T>
{
    Set<Object> keys(T entity);
    
    Object value(Object key, T entity);
}
