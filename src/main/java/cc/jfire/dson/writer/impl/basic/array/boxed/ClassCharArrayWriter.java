package cc.jfire.dson.writer.impl.basic.array.boxed;

import cc.jfire.dson.writer.TypeWriter;

import java.util.List;

public class ClassCharArrayWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        output.append('[');
        boolean hasComma = false;
        for (Character element : (Character[]) entity)
        {
            if (element != null)
            {
                output.append('"').append(element).append('"').append(',');
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
        return List.of((Character[]) entity);
    }
}
