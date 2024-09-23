package com.jfirer.dson.writer.impl.basic;

import com.jfirer.dson.writer.TypeWriter;

public class ByteWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        output.append(((Byte) entity).byteValue());
    }
}
