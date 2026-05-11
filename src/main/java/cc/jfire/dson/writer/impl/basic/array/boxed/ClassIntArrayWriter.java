package cc.jfire.dson.writer.impl.basic.array.boxed;

import cc.jfire.dson.writer.TypeWriter;

import java.util.List;

public class ClassIntArrayWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        output.append('[');
        boolean hasComma = false;
        for (Integer element : (Integer[]) entity)
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

    @Override
    public Object toJsonValue(Object entity)
    {
        return List.of((Integer[]) entity);
    }
}
