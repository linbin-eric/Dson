package com.jfirer.dson.writer.impl.basic;

import com.jfirer.dson.writer.TypeWriter;

public class DoubleWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        output.append(((Double) entity).doubleValue());
    }
}
