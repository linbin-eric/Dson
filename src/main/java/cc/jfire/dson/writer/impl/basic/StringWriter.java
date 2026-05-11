package cc.jfire.dson.writer.impl.basic;

import cc.jfire.dson.util.WriterUtil;
import cc.jfire.dson.writer.TypeWriter;

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

    @Override
    public Object toJsonValue(Object entity)
    {
        return entity;
    }
}
