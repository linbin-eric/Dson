package cc.jfire.dson.writer.impl.basic;

import cc.jfire.dson.writer.TypeWriter;

public class EnumOrdinalWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        int ordinal = ((Enum) entity).ordinal();
        output.append(ordinal);
    }

    @Override
    public Object toJsonValue(Object entity)
    {
        int ordinal = ((Enum) entity).ordinal();
        return ordinal;
    }
}
