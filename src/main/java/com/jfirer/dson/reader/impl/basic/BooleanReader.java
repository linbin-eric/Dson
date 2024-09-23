package com.jfirer.dson.reader.impl.basic;

import com.jfirer.dson.reader.JsonReader;
import com.jfirer.dson.reader.Stream;
import com.jfirer.dson.reader.TypeReader;

import java.lang.reflect.Type;

public class BooleanReader implements TypeReader
{
    @Override
    public void init(Type type, JsonReader jsonReader)
    {
    }

    @Override
    public Object fromString(Stream stream)
    {
        return stream.getBoolean();
    }
}
