package com.jfirer.dson.reader.impl;

import com.jfirer.dson.DsonContext;
import com.jfirer.dson.reader.Stream;
import com.jfirer.dson.reader.TypeReader;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

public class ArrayReader implements TypeReader
{
    private boolean     componentTypePrimitive = false;
    private Type        componentType;
    private TypeReader  componentReader;
    private DsonContext dsonContext;
    private DimInfo     dimInfo;

    class DimInfo
    {
        final int[] dims;
        final Class componentType;

        public DimInfo(int[] dims, Class componentType)
        {
            this.dims          = dims;
            this.componentType = componentType;
        }
    }

    @Override
    public void init(Type type, DsonContext dsonContext)
    {
        this.dsonContext = dsonContext;
        if (type instanceof GenericArrayType)
        {
            Type genericComponentType = ((GenericArrayType) type).getGenericComponentType();
            if (genericComponentType instanceof Class)
            {
                if (((Class) genericComponentType).isPrimitive())
                {
                    componentTypePrimitive = true;
                }
            }
            this.componentType = genericComponentType;
        }
        else if (type instanceof Class)
        {
            Class ckass         = (Class) type;
            Class componentType = ckass.getComponentType();
            if (componentType.isPrimitive())
            {
                componentTypePrimitive = true;
            }
            this.componentType = componentType;
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
        //未知具体的数组长度，每次数组增长长度为 8
        int step   = 8;
        int length = step;
        if (componentTypePrimitive)
        {
            int index = 0;
            if (componentType == int.class)
            {
                int[] array = new int[length];
                while (stream.parseArrayEnd() == false)
                {
                    if (index == length)
                    {
                        length += step;
                        array = Arrays.copyOf(array, length);
                    }
                    if (stream.isNextNullAndSkip())
                    {
                        ;
                    }
                    else
                    {
                        array[index] = stream.getInt();
                        index += 1;
                    }
                    stream.skipComma();
                }
                return Arrays.copyOf(array, index);
            }
            else if (componentType == short.class)
            {
                short[] array = new short[length];
                while (stream.parseArrayEnd() == false)
                {
                    if (index == length)
                    {
                        length += step;
                        array = Arrays.copyOf(array, length);
                    }
                    if (stream.isNextNullAndSkip())
                    {
                        ;
                    }
                    else
                    {
                        array[index] = stream.getShort();
                        index += 1;
                    }
                    stream.skipComma();
                }
                return Arrays.copyOf(array, index);
            }
            else if (componentType == byte.class)
            {
                byte[] array = new byte[length];
                while (stream.parseArrayEnd() == false)
                {
                    if (index == length)
                    {
                        length += step;
                        array = Arrays.copyOf(array, length);
                    }
                    if (stream.isNextNullAndSkip())
                    {
                        ;
                    }
                    else
                    {
                        array[index] = stream.getByte();
                        index += 1;
                    }
                    stream.skipComma();
                }
                return Arrays.copyOf(array, index);
            }
            else if (componentType == long.class)
            {
                long[] array = new long[length];
                while (stream.parseArrayEnd() == false)
                {
                    if (index == length)
                    {
                        length += step;
                        array = Arrays.copyOf(array, length);
                    }
                    if (stream.isNextNullAndSkip())
                    {
                        ;
                    }
                    else
                    {
                        array[index] = stream.getLong();
                        index += 1;
                    }
                    stream.skipComma();
                }
                return Arrays.copyOf(array, index);
            }
            else if (componentType == char.class)
            {
                char[] array = new char[length];
                while (stream.parseArrayEnd() == false)
                {
                    if (index == length)
                    {
                        length += step;
                        array = Arrays.copyOf(array, length);
                    }
                    if (stream.isNextNullAndSkip())
                    {
                        ;
                    }
                    else
                    {
                        array[index] = stream.getChar();
                        index += 1;
                    }
                    stream.skipComma();
                }
                return Arrays.copyOf(array, index);
            }
            else if (componentType == boolean.class)
            {
                boolean[] array = new boolean[length];
                while (stream.parseArrayEnd() == false)
                {
                    if (index == length)
                    {
                        length += step;
                        array = Arrays.copyOf(array, length);
                    }
                    if (stream.isNextNullAndSkip())
                    {
                        ;
                    }
                    else
                    {
                        array[index] = stream.getBoolean();
                        index += 1;
                    }
                    stream.skipComma();
                }
                return Arrays.copyOf(array, index);
            }
            else if (componentType == float.class)
            {
                float[] array = new float[length];
                while (stream.parseArrayEnd() == false)
                {
                    if (index == length)
                    {
                        length += step;
                        array = Arrays.copyOf(array, length);
                    }
                    if (stream.isNextNullAndSkip())
                    {
                        ;
                    }
                    else
                    {
                        array[index] = stream.getFloat();
                        index += 1;
                    }
                    stream.skipComma();
                }
                return Arrays.copyOf(array, index);
            }
            else if (componentType == double.class)
            {
                double[] array = new double[length];
                while (stream.parseArrayEnd() == false)
                {
                    if (index == length)
                    {
                        length += step;
                        array = Arrays.copyOf(array, length);
                    }
                    if (stream.isNextNullAndSkip())
                    {
                        ;
                    }
                    else
                    {
                        array[index] = stream.getDouble();
                        index += 1;
                    }
                    stream.skipComma();
                }
                return Arrays.copyOf(array, index);
            }
            else
            {
                throw new IllegalArgumentException();
            }
        }
        else
        {
            TypeReader typeReader = componentReader;
            if (typeReader == null)
            {
                componentReader = typeReader = dsonContext.parseReader(componentType);
            }
            Object[] array = null;
            if (componentType instanceof Class)
            {
                array = (Object[]) Array.newInstance((Class<?>) componentType, length);
            }
            else if (componentType instanceof ParameterizedType)
            {
                Type rawType = ((ParameterizedType) componentType).getRawType();
                array = (Object[]) Array.newInstance((Class<?>) rawType, length);
            }
            else if (componentType instanceof GenericArrayType)
            {
                DimInfo tmp = dimInfo;
                if (tmp == null)
                {
                    int  subDim        = 0;
                    Type componentType = this.componentType;
                    while (componentType instanceof GenericArrayType)
                    {
                        subDim += 1;
                        componentType = ((GenericArrayType) componentType).getGenericComponentType();
                    }
                    int[] dims = new int[1 + subDim];
                    dims[0] = length = step;
                    if (componentType instanceof Class)
                    {
                        array   = (Object[]) Array.newInstance((Class<?>) componentType, dims);
                        dimInfo = tmp = new DimInfo(dims, (Class) componentType);
                    }
                    else if (componentType instanceof ParameterizedType)
                    {
                        Type rawType = ((ParameterizedType) componentType).getRawType();
                        array   = (Object[]) Array.newInstance((Class<?>) rawType, dims);
                        dimInfo = tmp = new DimInfo(dims, (Class) rawType);
                    }
                    else
                    {
                        throw new IllegalArgumentException();
                    }
                }
                else
                {
                    array = (Object[]) Array.newInstance(tmp.componentType, tmp.dims);
                }
            }
            int index = 0;
            while (stream.parseArrayEnd() == false)
            {
                if (index == length)
                {
                    length += step;
                    array = Arrays.copyOf(array, length);
                }
                if (stream.isNextNullAndSkip())
                {
                    array[index] = null;
                }
                else
                {
                    array[index] = typeReader.fromString(stream);
                    index += 1;
                }
                stream.skipComma();
            }
            return Arrays.copyOf(array, index);
        }
    }
}
