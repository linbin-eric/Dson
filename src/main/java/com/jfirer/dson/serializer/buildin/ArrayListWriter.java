package com.jfirer.dson.serializer.buildin;

import com.jfirer.dson.serializer.JsonWriter;
import com.jfirer.dson.serializer.TypeWriter;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ArrayListWriter implements TypeWriter
{
    boolean elementTypeFinal = false;
    volatile TypeWriter elementWriter;
    private  JsonWriter jsonWriter;

    @Override
    public void initialize(JsonWriter writer, Type type)
    {
        jsonWriter = writer;
        if (type instanceof ParameterizedType)
        {
            Type argument = ((ParameterizedType) type).getActualTypeArguments()[0];
            if (Modifier.isFinal(((Class) argument).getModifiers()))
            {
                elementTypeFinal = true;
            }
        }
    }

    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        if (elementTypeFinal)
        {
            ArrayList arrayList = (ArrayList) entity;
            int       size      = arrayList.size();
            output.append('[');
            int length = output.length();
            for (int i = 0; i < size; i++)
            {
                Object each = arrayList.get(i);
                if (each != null)
                {
                    if (elementWriter == null)
                    {
                        elementWriter = jsonWriter.get(each.getClass());
                    }
                    elementWriter.toJson(each, output);
                    output.append(',');
                }
            }
            if (length != output.length())
            {
                output.setLength(output.length() - 1);
            }
            output.append(']');
        }
        else
        {
            ArrayList arrayList = (ArrayList) entity;
            int       size      = arrayList.size();
            output.append('[');
            int length = output.length();
            for (int i = 0; i < size; i++)
            {
                Object each = arrayList.get(i);
                if (each != null)
                {
                    jsonWriter.toJson(each, output);
                    output.append(',');
                }
            }
            if (length != output.length())
            {
                output.setLength(output.length() - 1);
            }
            output.append(']');
        }
    }
}
