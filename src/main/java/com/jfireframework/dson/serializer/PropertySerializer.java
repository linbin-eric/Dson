package com.jfireframework.dson.serializer;

import com.jfireframework.dson.util.StringOutput;

public interface PropertySerializer
{
    
    void initialize(Class<?> type, String property);
    
    /**
     * 如果有值，则序列化，返回true；否则返回false
     * 
     * @param host
     * @param output
     * @return
     */
    boolean serialize(Object host, StringOutput output);
}
