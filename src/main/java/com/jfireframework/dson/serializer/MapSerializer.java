package com.jfireframework.dson.serializer;

import java.util.Map;
import java.util.Set;

public interface MapSerializer extends Serializer
{
    Set<Object> keys(Map<?, ?> entity);
    
    Object value(Object key, Map<?, ?> entity);
}
