package com.jfirer.dson.writer.impl.basic.array;

import com.jfirer.dson.writer.TypeWriter;

public class FloatArrayWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        output.append('[');
        float[] arr = (float[]) entity;
        for (float element : arr)
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
