package com.jfirer.dson.reader.impl.basic;

import com.jfirer.dson.DsonContext;
import com.jfirer.dson.reader.Stream;
import com.jfirer.dson.reader.TypeReader;

import java.lang.reflect.Type;

public class FloatReader implements TypeReader
{
    @Override
    public void initialize(Type type, DsonContext dsonContext)
    {
    }

    @Override
    public Object fromString(Stream stream)
    {
        return stream.getFloat();
    }
}
