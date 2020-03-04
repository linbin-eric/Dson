package com.jfirer.dson.serializer.impl;

import com.jfirer.dson.serializer.JsonWriter;
import com.jfirer.dson.serializer.TypeWriter;
import com.jfirer.dson.util.WriterUtil;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

public class CollectionWriter implements TypeWriter
{
    private JsonWriter jsonWriter;
    private TypeWriter elementWriter;
    private boolean    elementString    = false;
    private boolean    elementTypeFinal = false;

    @Override
    public void initialize(JsonWriter serializer, Type type)
    {
        this.jsonWriter = serializer;
        if (type instanceof ParameterizedType)
        {
            Type elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
            if (elementType instanceof Class<?>)
            {
                if (elementType == String.class)
                {
                    elementString = true;
                }
                else if (Modifier.isFinal(((Class<?>) elementType).getModifiers()))
                {
                    elementTypeFinal = true;
                    elementWriter = serializer.get(elementType);
                }
            }
        }
    }

    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        if (entity == null)
        {
            return;
        }
        Collection<?> collection = (Collection<?>) entity;
        output.append('[');
        int length = output.length();
        if (elementString)
        {
            for (Object o : collection)
            {
                output.append('"');
                WriterUtil.writeString(output, (String) o);
                output.append('"').append(',');
            }
        }
        else if (elementTypeFinal)
        {
            for (Object each : collection)
            {
                if (elementWriter == null)
                {
                    elementWriter = jsonWriter.get(each.getClass());
                }
                elementWriter.toJson(each, output);
                output.append(',');
            }
        }
        else
        {
            for (Object each : collection)
            {
                jsonWriter.toJson(each, output);
                output.append(',');
            }
        }
        if (output.length() != length)
        {
            output.setLength(output.length() - 1);
        }
        output.append(']');
    }
}
