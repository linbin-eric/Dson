package cc.jfire.dson.reader.impl.basic;

import cc.jfire.dson.DsonContext;
import cc.jfire.dson.reader.Stream;
import cc.jfire.dson.reader.TypeReader;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

public class LongReader implements TypeReader
{
    @Override
    public void initialize(Type type, DsonContext dsonContext, Map<TypeVariable<?>, Type> typeVariableContext)
    {
    }

    @Override
    public Object fromString(Stream stream, Map<TypeVariable<?>, Type> typeVariableContext)
    {
        return stream.getLong();
    }
}
