package com.jfireframework.dson.serializer;

public interface BeanSerializer<T> extends Serializer<T>
{
    PropertySerializer<T>[] propertySerializers();
}
