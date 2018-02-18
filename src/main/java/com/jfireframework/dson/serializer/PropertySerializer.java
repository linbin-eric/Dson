package com.jfireframework.dson.serializer;

import com.jfireframework.dson.StringOutput;

public interface PropertySerializer
{
    String propertyName();
    
    Object propertyValue(Object entity);
    
    void initialize(Class<?> type, String property);
    
    void serialize(Object propertyValue, StringOutput output);
}
