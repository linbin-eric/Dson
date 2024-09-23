package com.jfirer.dson.reader.impl.basic;

import com.jfirer.dson.reader.JsonReader;
import com.jfirer.dson.reader.Stream;
import com.jfirer.dson.reader.TypeReader;

import java.lang.reflect.Type;

public class EnumNameReader implements TypeReader
{
    private Class type;

    @Override
    public void init(Type type, JsonReader jsonReader)
    {
        this.type = (Class) type;
    }

    @Override
    public Object fromString(Stream stream)
    {
        return Enum.valueOf(type, stream.getStringValue());
    }
}
