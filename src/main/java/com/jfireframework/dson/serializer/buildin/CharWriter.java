package com.jfireframework.dson.serializer.buildin;

import com.jfireframework.dson.serializer.JsonWriter;
import com.jfireframework.dson.serializer.TypeWriter;

import java.lang.reflect.Type;

public class CharWriter implements TypeWriter
{
    @Override
    public void initialize(JsonWriter writer, Type type)
    {
    }

    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        output.append('"').append(((Character) entity).charValue()).append('"');
    }
}
