package com.jfirer.dson.writer.impl.basic;

import com.jfirer.dson.writer.TypeWriter;

public class IntegerWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        output.append(((Integer) entity).intValue());
    }
}
