package cc.jfire.dson.writer.impl.basic.array.boxed;

import cc.jfire.dson.writer.TypeWriter;

import java.util.List;

public class ClassLongArrayWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        output.append('[');
        boolean hasComma = false;
        for (Long element : (Long[]) entity)
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
        return List.of((Long[]) entity);
    }
}
