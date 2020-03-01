package com.jfirer.dson.serializer.impl;

import com.jfirer.dson.serializer.JsonWriter;
import com.jfirer.dson.serializer.TypeWriter;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

public class MapWriter implements TypeWriter
{
    private          JsonWriter jsonWriter;
    private volatile TypeWriter valueWriter;
    private          boolean    valueTypeFinal = false;

    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        if (entity == null)
        {
            return;
        }
        output.append('{');
        int length = output.length();
        for (Entry<?, ?> entry : ((Map<?, ?>) entity).entrySet())
        {
            Object entryValue = entry.getValue();
            if (entryValue != null)
            {
                output.append('"').append(entry.getKey()).append("\":");
                if (valueTypeFinal)
                {
                    if (valueWriter == null)
                    {
                        valueWriter = jsonWriter.get(entry.getValue().getClass());
                    }
                    valueWriter.toJson(entryValue, output);
                }
                else
                {
                    jsonWriter.toJson(entryValue, output);
                }
                output.append(',');
            }
        }
        if (length != output.length())
        {
            output.setLength(output.length() - 1);
        }
        output.append('}');
    }

    @Override
    public void initialize(JsonWriter jsonWriter, Type type)
    {
        this.jsonWriter = jsonWriter;
        if (type instanceof ParameterizedType)
        {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            Type   valueType           = actualTypeArguments[1];
            if (valueType instanceof Class<?>)
            {
                if (Modifier.isFinal(((Class<?>) valueType).getModifiers()))
                {
                    valueTypeFinal = true;
                }
            }
        }
        else if (type instanceof Class<?>)
        {
            ;
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }
}
