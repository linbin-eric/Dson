package com.jfirer.dson.writer.impl.basic.array;

import com.jfirer.dson.writer.TypeWriter;

public class ShortArrayWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        output.append('[');
        short[] arr = (short[]) entity;
        for (short element : arr)
        {
            output.append(element).append(',');
        }
        if (arr.length != 0)
        {
            output.setLength(output.length() - 1);
        }
        output.append(']');
    }
}
