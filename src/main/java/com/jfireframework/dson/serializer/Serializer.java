package com.jfireframework.dson.serializer;

import com.jfireframework.dson.JsonProcessor;
import com.jfireframework.dson.StringOutput;

public interface Serializer<T>
{
    void initialize(JsonProcessor jsonProcessor, Class<T> type);
    
    void serialize(T entity, StringOutput output);
}
