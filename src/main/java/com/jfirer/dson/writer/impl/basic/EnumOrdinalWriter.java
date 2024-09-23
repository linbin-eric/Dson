package com.jfirer.dson.writer.impl.basic;

import com.jfirer.dson.writer.JsonWriter;
import com.jfirer.dson.writer.TypeWriter;

import java.lang.reflect.Type;

public class EnumOrdinalWriter implements TypeWriter
{

    @Override
    public void initialize(JsonWriter writer, Type type)
    {
    }

    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        int ordinal = ((Enum) entity).ordinal();
        output.append(ordinal);
    }
}
