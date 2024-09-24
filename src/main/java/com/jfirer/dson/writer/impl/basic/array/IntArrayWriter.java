package com.jfirer.dson.writer.impl.basic.array;

import com.jfirer.dson.writer.TypeWriter;

public class IntArrayWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        output.append('[');
        int[] arr = (int[]) entity;
        for (int element : arr)
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
