package cc.jfire.dson.writer.impl;

import cc.jfire.dson.DsonContext;
import cc.jfire.dson.writer.TypeWriter;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionWriter implements TypeWriter
{
    private DsonContext dsonContext;
    private TypeWriter  elementWriter;
    private boolean     elementTypeFinal = false;

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
                elementWriter    = dsonContext.parseWriter(elementType);
            }
        }
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

    @Override
    public Object toJsonValue(Object entity)
    {
        Collection<?> collection = (Collection<?>) entity;
        List<Object>  list       = new ArrayList<>();
        TypeWriter    writer     = null;
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
                    list.add(writer.toJsonValue(each));
                }
                else
                {
                    list.add(dsonContext.parseWriter(each.getClass()).toJsonValue(each));
                }
            }
        }
        return list;
    }
}
