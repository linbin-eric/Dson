package com.jfirer.dson.serializer.impl;

import com.jfirer.dson.serializer.JsonWriter;
import com.jfirer.dson.serializer.TypeWriter;

import java.lang.reflect.Type;

public class EnumWriter implements TypeWriter
{

    @Override
    public void initialize(JsonWriter serializer, Type type)
    {
    }

    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        Enum<?> instance = (Enum<?>) entity;
        output.append('"').append(instance.name()).append('"');
    }
}
