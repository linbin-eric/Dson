package com.jfirer.dson.writer.impl.basic;

import com.jfirer.dson.writer.JsonWriter;
import com.jfirer.dson.writer.TypeWriter;

import java.lang.reflect.Type;

public class ShortWriter implements TypeWriter
{
    @Override
    public void initialize(JsonWriter writer, Type type)
    {
    }

    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        output.append(((Short) entity).shortValue());
    }
}
