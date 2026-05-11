package cc.jfire.dson.writer.impl.basic;

import cc.jfire.dson.writer.TypeWriter;

public class FloatWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        output.append(((Float) entity).floatValue());
    }

    @Override
    public Object toJsonValue(Object entity)
    {
        return entity;
    }
}
