package cc.jfire.dson.reader.impl;

import cc.jfire.dson.reader.Stream;
import cc.jfire.dson.reader.TypeReader;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

public class UnKnowTypeReader implements TypeReader
{
    @Override
    public Object fromString(Stream stream, Map<TypeVariable<?>, Type> typeVariableContext)
    {
        return stream.readUnKnowType();
    }
}
