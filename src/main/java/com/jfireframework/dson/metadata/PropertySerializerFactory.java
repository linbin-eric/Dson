package com.jfireframework.dson.metadata;

import com.jfireframework.dson.serializer.PropertySerializer;

public interface PropertySerializerFactory
{
    <T> PropertySerializer<T> get(Class<T> type, String property);
}
