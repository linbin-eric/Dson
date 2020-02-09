package com.jfireframework.dson.serializer;

import java.lang.reflect.Type;

public interface JsonWriter
{
    int registerSerializeDescriptor(TypeWriter serializeDescriptor);

    TypeWriter get(int index);

    /**
     * 获取某一个类型的序列化描述器
     *
     * @param type
     * @return
     */
    TypeWriter get(Type type);

    /**
     * json输出一个对象，其内部是依靠序列化描述器来实现json输出
     *
     * @param entity
     * @param output
     */
    void toJson(Object entity, StringBuilder output);
}
