package com.jfirer.dson.writer.impl;

import com.jfirer.dson.writer.TypeWriter;

public class EnumWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        Enum<?> instance = (Enum<?>) entity;
        output.append('"').append(instance.name()).append('"');
    }
}
