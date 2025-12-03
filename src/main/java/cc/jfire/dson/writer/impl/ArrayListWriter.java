package cc.jfire.dson.writer.impl;

import cc.jfire.dson.DsonContext;
import cc.jfire.dson.writer.TypeWriter;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ArrayListWriter implements TypeWriter
{
    boolean     elementTypeFinal = false;
    Class       elementType;
    TypeWriter  elementWriter;
    DsonContext dsonContext;

    @Override
    public void initialize(Type type, DsonContext dsonContext)
    {
        this.dsonContext = dsonContext;
        if (type instanceof ParameterizedType)
        {
            Type argument = ((ParameterizedType) type).getActualTypeArguments()[0];
            if (argument instanceof Class && Modifier.isFinal(((Class) argument).getModifiers()))
            {
                elementTypeFinal = true;
                elementType      = (Class) argument;
                elementWriter    = dsonContext.parseWriter((Class) argument);
            }
        }
    }

    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        TypeWriter writer = null;
        if (elementTypeFinal)
        {
            writer = this.elementWriter;
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
                if (elementTypeFinal)
                {
                    writer.toJson(each, output);
                }
                else
                {
                    dsonContext.parseWriter(each.getClass()).toJson(each, output);
                }
                output.append(',');
                hasComma = true;
            }
        }
        if (hasComma)
        {
            output.setLength(output.length() - 1);
        }
        output.append(']');
    }
}
