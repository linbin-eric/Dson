package com.jfirer.dson.writer.impl;

import com.jfirer.dson.writer.JsonWriter;
import com.jfirer.dson.writer.TypeWriter;
import com.jfirer.dson.writer.Writer;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

public class MapWriter implements TypeWriter
{
    private JsonWriter jsonWriter;
    private TypeWriter valueWriter;
    private Type       valueType;
    private boolean    valueTypeFinal = false;

    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        if (entity == null)
        {
            return;
        }
        output.append('{');
        Writer writer;
        boolean hasComma = false;
        if (valueTypeFinal)
        {
            writer = this.valueWriter;
            if (writer == null)
            {
                this.valueWriter = (TypeWriter) (writer = jsonWriter.get(valueType));
            }
        }
        else
        {
            writer = jsonWriter;
        }
        for (Entry<?, ?> entry : ((Map<?, ?>) entity).entrySet())
        {
            Object entryValue = entry.getValue();
            if (entryValue != null)
            {
                output.append('"').append(entry.getKey()).append("\":");
                writer.toJson(entryValue, output);
                output.append(',');
                hasComma = true;
            }
        }
        if (hasComma)
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
            if (isTypeFinal(valueType))
            {
                valueTypeFinal = true;
                this.valueType = valueType;
            }
        }
        else if (type instanceof Class<?>)
        {
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }

    private boolean isTypeFinal(Type type)
    {
        if (type instanceof Class<?> && Modifier.isFinal(((Class<?>) type).getModifiers()))
        {
            return true;
        }
        else if (type instanceof GenericArrayType)
        {
            return true;
        }
        else if (type instanceof ParameterizedType && Modifier.isFinal(((Class) ((ParameterizedType) type).getRawType()).getModifiers()))
        {
            return true;
        }
        return false;
    }
}
