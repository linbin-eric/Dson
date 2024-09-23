package com.jfirer.dson.writer.impl.basic;

import com.jfirer.dson.writer.JsonWriter;
import com.jfirer.dson.writer.TypeWriter;
import com.jfirer.dson.writer.Writer;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ArrayListWriter implements TypeWriter
{
    boolean    elementTypeFinal = false;
    Class      elementType;
    TypeWriter elementWriter;
    JsonWriter jsonWriter;

    @Override
    public void initialize(JsonWriter writer, Type type)
    {
        jsonWriter = writer;
        if (type instanceof ParameterizedType)
        {
            Type argument = ((ParameterizedType) type).getActualTypeArguments()[0];
            if (argument instanceof Class && Modifier.isFinal(((Class) argument).getModifiers()))
            {
                elementTypeFinal = true;
                elementType      = (Class) argument;
            }
        }
    }

    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        Writer writer;
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
        ArrayList arrayList = (ArrayList) entity;
        int       size      = arrayList.size();
        output.append('[');
        boolean hasComma = false;
        for (int i = 0; i < size; i++)
        {
            Object each = arrayList.get(i);
            if (each != null)
            {
                writer.toJson(each, output);
                output.append(',');
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
