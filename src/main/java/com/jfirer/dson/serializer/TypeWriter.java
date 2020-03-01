package com.jfirer.dson.serializer;

import java.lang.reflect.Type;

public interface TypeWriter
{
    void initialize(JsonWriter writer, Type type);

    /**
     * 将对象json输出到output中
     *
     * @param entity
     * @param output
     * @return
     */
    void toJson(Object entity, StringBuilder output);
}
