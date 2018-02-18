package com.jfireframework.dson.metadata;

import com.jfireframework.dson.serializer.PropertySerializer;

public interface PropertySerializerFactory
{
    PropertySerializer get(Class<?> type, String property);
}
