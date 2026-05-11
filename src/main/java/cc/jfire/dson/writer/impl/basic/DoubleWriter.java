package cc.jfire.dson.writer.impl.basic;

import cc.jfire.dson.writer.TypeWriter;

public class DoubleWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        output.append(((Double) entity).doubleValue());
    }

    @Override
    public Object toJsonObject(Object entity)
    {
        return entity;
    }
}
