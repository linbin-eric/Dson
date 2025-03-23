package com.jfirer.dson.writer.impl.basic.array.boxed;

import com.jfirer.dson.util.WriterUtil;
import com.jfirer.dson.writer.TypeWriter;

public class StringArrayWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        output.append('[');
        boolean hasComma = false;
        for (String element : (String[]) entity)
        {
            if (element != null)
            {
                output.append('"');
                WriterUtil.writeString(output, element);
                output.append('"');
                output.append(',');
//                output.append('"').append(element).append('"').append(',');
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
