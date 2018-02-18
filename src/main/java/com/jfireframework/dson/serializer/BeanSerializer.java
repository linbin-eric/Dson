package com.jfireframework.dson.serializer;

public interface BeanSerializer extends Serializer
{
    PropertySerializer[] propertySerializers();
}
