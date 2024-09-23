package com.jfirer.dson.writer.impl.basic;

import com.jfirer.dson.util.WriterUtil;
import com.jfirer.dson.writer.TypeWriter;

public class StringWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        if (entity == null)
        {
            return;
        }
        output.append('"');
        WriterUtil.writeString(output, (String) entity);
        output.append('"');
    }
}
