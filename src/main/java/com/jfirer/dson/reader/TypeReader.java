package com.jfirer.dson.reader;

import com.jfirer.dson.DsonContext;

import java.lang.reflect.Type;

public interface TypeReader
{
    void init(Type type, DsonContext dsonContext);

    Object fromString(Stream stream);
}
