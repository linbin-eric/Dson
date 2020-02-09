package com.jfireframework.dson.serializer.impl;

import com.jfireframework.dson.serializer.JsonWriter;
import com.jfireframework.dson.serializer.TypeWriter;

import java.lang.reflect.Type;

public class EnumWriter implements TypeWriter
{

    @Override
    public void initialize(JsonWriter serializer, Type type)
    {
        ;
    }

    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        Enum<?> instance = (Enum<?>) entity;
        output.append('"').append(instance.name()).append('"');
    }
}
