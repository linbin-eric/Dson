package com.jfireframework.dson.deserializer;

import java.lang.reflect.Type;
import java.util.Map;

public interface Deserializer
{
    DeserializeDescriptor describe(Type type);
    
    DeserializeDescriptor describe(Type type, Map<Type, DeserializeDescriptor> map);
    
    <T> T deserialize(Type type, String json);
}
