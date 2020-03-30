package com.jfirer.dson.writer;

import java.lang.reflect.Type;

public interface TypeWriter extends Writer
{
    void initialize(JsonWriter writer, Type type);
}
