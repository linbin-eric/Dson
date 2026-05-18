package cc.jfire.dson.reader.impl.basic;

import cc.jfire.dson.reader.Stream;
import cc.jfire.dson.reader.TypeReader;


public class LongReader implements TypeReader
{
    @Override
    public Object fromString(Stream stream)
    {
        return stream.getLong();
    }
}
