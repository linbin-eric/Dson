package com.jfirer.dson.reader;

import java.lang.reflect.Type;

public interface TypeReader
{
    void init(Type type, JsonReader jsonReader);

    Object fromString(Stream stream);
}
