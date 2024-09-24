package com.jfirer.dson.writer.impl.basic.array.boxed;

import com.jfirer.dson.writer.TypeWriter;

public class ClassShortArrayWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        output.append('[');
        boolean hasComma = false;
        for (Short element : (Short[]) entity)
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
}
