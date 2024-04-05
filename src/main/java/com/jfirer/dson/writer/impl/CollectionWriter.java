package com.jfirer.dson.writer.impl;

import com.jfirer.dson.writer.JsonWriter;
import com.jfirer.dson.writer.TypeWriter;
import com.jfirer.dson.writer.Writer;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

public class CollectionWriter implements TypeWriter
{
    private JsonWriter jsonWriter;
    private TypeWriter elementWriter;
    private boolean    elementTypeFinal = false;
    private Class      elementType;

    @Override
    public void initialize(JsonWriter serializer, Type type)
    {
        this.jsonWriter = serializer;
        if (type instanceof ParameterizedType)
        {
            Type elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
            if (elementType instanceof Class<?> && Modifier.isFinal(((Class<?>) elementType).getModifiers()))
            {
                elementTypeFinal = true;
                this.elementType = (Class) elementType;
            }
        }
    }

    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        Collection<?> collection = (Collection<?>) entity;
        output.append('[');
        boolean hasComma = false;
        Writer  writer;
        if (elementTypeFinal)
        {
            writer = this.elementWriter;
            if (elementWriter == null)
            {
                this.elementWriter = (TypeWriter) (writer = jsonWriter.get(elementType));
            }
        }
        else
        {
            writer = jsonWriter;
        }
        for (Object each : collection)
        {
            if (each != null)
            {
                writer.toJson(each, output);
                output.append(',');
            }
            else
            {
                output.append("null,");
            }
            hasComma = true;
        }
        if (hasComma)
        {
            output.setLength(output.length() - 1);
        }
        output.append(']');
    }
}
