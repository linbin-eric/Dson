package cc.jfire.dson.writer.impl.basic;

import cc.jfire.dson.writer.TypeWriter;

public class LongWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        output.append(((Long) entity).longValue());
    }
}
