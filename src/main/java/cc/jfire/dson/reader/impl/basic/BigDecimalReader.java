package cc.jfire.dson.reader.impl.basic;

import cc.jfire.dson.reader.Stream;
import cc.jfire.dson.reader.TypeReader;

import java.math.BigDecimal;

public class BigDecimalReader implements TypeReader
{
    @Override
    public Object fromString(Stream stream)
    {
        return new BigDecimal(stream.getWholeValueAsString());
    }
}
