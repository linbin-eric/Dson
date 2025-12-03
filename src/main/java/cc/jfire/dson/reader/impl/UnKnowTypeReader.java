package cc.jfire.dson.reader.impl;

import cc.jfire.dson.reader.Stream;
import cc.jfire.dson.reader.TypeReader;

public class UnKnowTypeReader implements TypeReader
{
    @Override
    public Object fromString(Stream stream)
    {
        return stream.readUnKnowType();
    }
}
