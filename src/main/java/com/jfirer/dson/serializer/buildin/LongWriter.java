package com.jfirer.dson.serializer.buildin;

import com.jfirer.dson.serializer.JsonWriter;
import com.jfirer.dson.serializer.TypeWriter;

import java.lang.reflect.Type;

public class LongWriter implements TypeWriter
{
    @Override
    public void initialize(JsonWriter writer, Type type)
    {
    }

    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        output.append(((Long) entity).longValue());
    }
}
