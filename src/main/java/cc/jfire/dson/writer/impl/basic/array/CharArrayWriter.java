package cc.jfire.dson.writer.impl.basic.array;

import cc.jfire.dson.writer.TypeWriter;

import java.util.List;

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

    @Override
    public Object toJsonValue(Object entity)
    {
        return List.of((char[]) entity);
    }
}
