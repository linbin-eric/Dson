package cc.jfire.dson.writer.impl;

import cc.jfire.dson.writer.TypeWriter;

public class EnumWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        Enum<?> instance = (Enum<?>) entity;
        output.append('"').append(instance.name()).append('"');
    }

    @Override
    public Object toJsonObject(Object entity)
    {
        Enum<?> instance = (Enum<?>) entity;
        return instance.name();
    }
}
