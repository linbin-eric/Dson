package cc.jfire.dson.writer.impl.basic;

import cc.jfire.dson.writer.TypeWriter;

public class BooleanWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        output.append(((Boolean) entity).booleanValue());
    }
}
