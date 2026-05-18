package cc.jfire.dson.reader.impl.basic;

import cc.jfire.dson.reader.ReaderContext;
import cc.jfire.dson.reader.Stream;
import cc.jfire.dson.reader.TypeReader;

import java.lang.reflect.Type;

public class EnumNameReader implements TypeReader
{
    private Class type;

    @Override
    public void initialize(Type type, ReaderContext readerContext)
    {
        this.type = (Class) type;
    }

    @Override
    public Object fromString(Stream stream)
    {
        return Enum.valueOf(type, stream.getStringValue());
    }
}
