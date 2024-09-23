package com.jfirer.dson.writer.impl.basic;

import com.jfirer.dson.writer.TypeWriter;

public class EnumOrdinalWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        int ordinal = ((Enum) entity).ordinal();
        output.append(ordinal);
    }
}
