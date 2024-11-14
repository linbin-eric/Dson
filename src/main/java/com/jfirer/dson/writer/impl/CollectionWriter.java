package com.jfirer.dson.writer.impl;

import com.jfirer.dson.DsonContext;
import com.jfirer.dson.util.InitializeStatusHolder;
import com.jfirer.dson.writer.TypeWriter;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

public class CollectionWriter extends InitializeStatusHolder.InitializeStatusHolderImpl implements TypeWriter
{
    private DsonContext dsonContext;
    private TypeWriter  elementWriter;
    private boolean     elementTypeFinal = false;
    private Class       elementType;

    @Override
    public void initialize(Type type, DsonContext dsonContext)
    {
        this.dsonContext = dsonContext;
        if (type instanceof ParameterizedType)
        {
            Type elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
            if (elementType instanceof Class<?> && Modifier.isFinal(((Class<?>) elementType).getModifiers()))
            {
                elementTypeFinal = true;
                this.elementType = (Class) elementType;
                elementWriter    = dsonContext.parseWriter(elementType);
            }
        }
        setInitialized();
    }

    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        Collection<?> collection = (Collection<?>) entity;
        output.append('[');
        boolean    hasComma = false;
        TypeWriter writer   = null;
        if (elementTypeFinal)
        {
            writer = this.elementWriter;
        }
        else
        {
        }
        for (Object each : collection)
        {
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
