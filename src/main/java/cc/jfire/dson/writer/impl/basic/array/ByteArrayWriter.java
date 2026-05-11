package cc.jfire.dson.writer.impl.basic.array;

import cc.jfire.dson.writer.TypeWriter;

import java.util.List;

public class ByteArrayWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        output.append('[');
        byte[] arr = (byte[]) entity;
        for (byte element : arr)
        {
            output.append(element).append(',');
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
        return List.of((byte[]) entity);
    }
}
