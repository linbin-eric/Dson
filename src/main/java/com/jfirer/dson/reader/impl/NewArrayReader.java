package com.jfirer.dson.reader.impl;

import com.jfirer.dson.DsonContext;
import com.jfirer.dson.reader.Stream;
import com.jfirer.dson.reader.TypeReader;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

public class NewArrayReader implements TypeReader
{
    private Class      componentType;
    private TypeReader componentReader;

    @Override
    public void init(Type type, DsonContext dsonContext)
    {
        Type origin = type;
        if (type instanceof GenericArrayType)
        {
            int dim = 0;
            while (type instanceof GenericArrayType genericArrayType)
            {
                dim++;
                type = genericArrayType.getGenericComponentType();
            }
            if (type instanceof ParameterizedType parameterizedType)
            {
                Class rawType = (Class) parameterizedType.getRawType();
                componentType = rawType;
                for (int i = 0; i < dim - 1; i++)
                {
                    componentType = Array.newInstance(componentType, 0).getClass();
                }
                componentReader = dsonContext.parseReader(((GenericArrayType) origin).getGenericComponentType());
            }
            else
            {
                throw new IllegalStateException("代码不应该运行到这里");
            }
        }
        else if (type instanceof Class ckazz)
        {
            componentType   = ckazz.getComponentType();
            componentReader = dsonContext.parseReader(componentType);
        }
        else
        {
            throw new IllegalArgumentException(type.toString());
        }
    }

    @Override
    public Object fromString(Stream stream)
    {
        stream.startParseArray();
        int      count = 0;
        Object[] array = (Object[]) Array.newInstance(componentType, 16);
        while (stream.parseArrayEnd() == false)
        {
            if (count == array.length)
            {
                array = Arrays.copyOf(array, array.length * 2);
            }
            if (stream.isNextNullAndSkip())
            {
                ;
            }
            else
            {
                array[count] = componentReader.fromString(stream);
                count += 1;
            }
            stream.skipComma();
        }
        return Arrays.copyOf(array, count);
    }
}
