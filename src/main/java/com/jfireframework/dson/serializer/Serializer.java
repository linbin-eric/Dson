package com.jfireframework.dson.serializer;

import com.jfireframework.dson.JsonProcessor;
import com.jfireframework.dson.StringOutput;

public interface Serializer
{
    void initialize(JsonProcessor jsonProcessor, Class<?> type);
    
    void serialize(Object entity, StringOutput output);
}
