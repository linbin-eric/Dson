package com.jfireframework.dson.deserializer.impl;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import com.jfireframework.dson.deserializer.DeserializeDescriptor;
import com.jfireframework.dson.deserializer.Deserializer;
import com.jfireframework.dson.metadata.json.DsonObject;
import com.jfireframework.dson.metadata.json.Element;
import com.jfireframework.dson.metadata.json.Entry;
import com.jfireframework.dson.metadata.json.JsonArray;
import com.jfireframework.dson.metadata.json.JsonValueType;
import com.jfireframework.dson.metadata.parse.Lexer;

public class ArrayDeserializeDescriptor implements DeserializeDescriptor
{
    private DeserializeDescriptor elementDescriber;
    private Class<?>              componentType;
    
    @Override
    public void initialize(Type type, Deserializer deserializer, Map<Type, DeserializeDescriptor> map)
    {
        if (type instanceof Class<?>)
        {
            Class<?> arrayType = (Class<?>) type;
            componentType = arrayType.getComponentType();
            elementDescriber = deserializer.describe(componentType, map);
        }
        else if (type instanceof GenericArrayType)
        {
            componentType = (Class<?>) ((ParameterizedType) ((GenericArrayType) type).getGenericComponentType()).getRawType();
            elementDescriber = deserializer.describe(((GenericArrayType) type).getGenericComponentType(), map);
        }
        else
        {
            throw new IllegalArgumentException("非法类型:" + type);
        }
    }
    
    @Override
    public Object deserialize(DsonObject dsonObject)
    {
        JsonArray jsonArray = (JsonArray) dsonObject;
        Object[] array = (Object[]) Array.newInstance(componentType, jsonArray.getElements().size());
        int index = 0;
        for (Element element : jsonArray.getElements())
        {
            array[index] = elementDescriber.deserialize(element);
            index += 1;
        }
        return array;
    }
    
    @Override
    public Object deserialize(String json)
    {
        return deserialize(new Lexer(json).parse());
    }
    
    @Override
    public Object deserialize(Entry entry)
    {
        if (entry.getValueType() != JsonValueType.ARRAY)
        {
            throw new IllegalArgumentException();
        }
        return deserialize((JsonArray) entry.getValue());
    }
    
    @Override
    public Object deserialize(Element element)
    {
        if (element.getValueType() != JsonValueType.ARRAY)
        {
            throw new IllegalArgumentException();
        }
        return deserialize((JsonArray) element.getValue());
    }
    
}
