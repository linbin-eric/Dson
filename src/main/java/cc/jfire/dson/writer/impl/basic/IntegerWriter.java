package cc.jfire.dson.writer.impl.basic;

import cc.jfire.dson.writer.TypeWriter;

public class IntegerWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        output.append(((Integer) entity).intValue());
    }

    @Override
    public Object toJsonValue(Object entity)
    {
        return entity;
    }
}
