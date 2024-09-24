package com.jfirer.dson.writer.impl.basic.array.boxed;

import com.jfirer.dson.writer.TypeWriter;

public class ClassByteArrayWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        output.append('[');
        boolean hasComma = false;
        for (Byte element : (Byte[]) entity)
        {
            if (element != null)
            {
                output.append(element).append(',');
                hasComma = true;
            }
        }
        if (hasComma)
        {
            output.setLength(output.length() - 1);
        }
        output.append(']');
    }
}
