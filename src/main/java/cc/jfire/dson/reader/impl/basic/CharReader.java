package cc.jfire.dson.reader.impl.basic;

import cc.jfire.dson.DsonContext;
import cc.jfire.dson.reader.Stream;
import cc.jfire.dson.reader.TypeReader;

import java.lang.reflect.Type;

public class CharReader implements TypeReader
{
    @Override
    public void initialize(Type type, DsonContext dsonContext)
    {
    }

    @Override
    public Object fromString(Stream stream)
    {
        return stream.getChar();
    }
}
