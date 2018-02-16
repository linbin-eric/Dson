package com.jfireframework.dson.serializer;

import com.jfireframework.dson.StringOutput;

public interface PropertySerializer<T>
{
    String propertyName();
    
    Object propertyValue(T entity);
    
    void initialize(Class<T> type, String property);
    
    void serialize(Object propertyValue, StringOutput output);
}
