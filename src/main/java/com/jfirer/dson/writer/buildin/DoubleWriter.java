package com.jfirer.dson.writer.buildin;

import com.jfirer.dson.writer.JsonWriter;
import com.jfirer.dson.writer.TypeWriter;

import java.lang.reflect.Type;

public class DoubleWriter implements TypeWriter
{
    @Override
    public void initialize(JsonWriter writer, Type type)
    {
    }

    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        output.append(((Double) entity).doubleValue());
    }
}
