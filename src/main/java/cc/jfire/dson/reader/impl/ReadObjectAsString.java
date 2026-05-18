package cc.jfire.dson.reader.impl;

import cc.jfire.dson.reader.Stream;
import cc.jfire.dson.reader.TypeReader;

public class ReadObjectAsString implements TypeReader
{
    @Override
    public Object fromString(Stream stream)
    {
        return stream.getWholeValueAsString();
    }
}
