package cc.jfire.dson.writer.impl.basic.array;

import cc.jfire.dson.writer.TypeWriter;

import java.util.List;

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

    @Override
    public Object toJsonValue(Object entity)
    {
        return List.of((float[]) entity);
    }
}
