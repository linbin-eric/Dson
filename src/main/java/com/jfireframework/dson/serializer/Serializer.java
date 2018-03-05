package com.jfireframework.dson.serializer;

import java.lang.reflect.Type;
import java.util.Map;
import com.jfireframework.dson.util.StringOutput;

public interface Serializer
{
    SerializeDescriptor describe(Type type);
    
    SerializeDescriptor describe(Type type, Map<Type, SerializeDescriptor> map);
    
    void serialize(Object entity, StringOutput output);
    
    void serializeWithoutDoubleQuotes(Object entity, StringOutput output);
}
