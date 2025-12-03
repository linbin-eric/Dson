package cc.jfire.dson.writer.impl;

import cc.jfire.dson.DsonContext;
import cc.jfire.dson.writer.TypeWriter;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

public class MapWriter implements TypeWriter
{
    private DsonContext dsonContext;
    private TypeWriter  valueWriter;
    private boolean     valueTypeFinal = false;

    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        if (entity == null)
        {
            return;
        }
        output.append('{');
        TypeWriter writer   = null;
        boolean    hasComma = false;
        if (valueTypeFinal)
        {
            writer = this.valueWriter;
        }
        else
        {
            ;
        }
        for (Entry<?, ?> entry : ((Map<?, ?>) entity).entrySet())
        {
            Object entryValue = entry.getValue();
            if (entryValue != null)
            {
                output.append('"').append(entry.getKey()).append("\":");
                if (valueTypeFinal)
                {
                    writer.toJson(entryValue, output);
                }
                else
                {
                }
                dsonContext.parseWriter(entryValue.getClass()).toJson(entryValue, output);
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
    public void initialize(Type type, DsonContext dsonContext)
    {
        this.dsonContext = dsonContext;
        if (type instanceof ParameterizedType)
        {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            Type   valueType           = actualTypeArguments[1];
            if (isTypeFinal(valueType))
            {
                valueTypeFinal = true;
                valueWriter    = dsonContext.parseWriter(valueType);
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
