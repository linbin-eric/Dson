package cc.jfire.dson.writer.impl;

import cc.jfire.dson.writer.TypeWriter;

public class BigDecimalWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        output.append(((java.math.BigDecimal) entity).toPlainString());
    }
}
