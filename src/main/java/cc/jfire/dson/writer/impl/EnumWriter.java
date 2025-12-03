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
}
