package com.jfirer.dson.writer.impl.basic.array;

import com.jfirer.dson.writer.TypeWriter;

public class DoubleArrayWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        output.append('[');
        double[] arr = (double[]) entity;
        for (double element : arr)
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
