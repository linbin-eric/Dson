package com.jfirer.dson.writer.impl.basic.array;

import com.jfirer.dson.writer.TypeWriter;

public class CharArrayWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        output.append('[');
        char[] arr = (char[]) entity;
        for (char element : arr)
        {
            output.append('"').append(element).append('"').append(',');
        }
        if (arr.length != 0)
        {
            output.setLength(output.length() - 1);
        }
        output.append(']');
    }
}
