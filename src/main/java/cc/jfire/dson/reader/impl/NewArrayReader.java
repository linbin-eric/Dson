package cc.jfire.dson.reader.impl;

import cc.jfire.dson.DsonContext;
import cc.jfire.dson.reader.Stream;
import cc.jfire.dson.reader.TypeReader;
import cc.jfire.dson.reader.support.TypeResolver;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Map;

public class NewArrayReader implements TypeReader
{
    private Class      componentType;
    private TypeReader componentReader;

    @Override
    public void initialize(Type type, DsonContext dsonContext, Map<TypeVariable<?>, Type> typeVariableContext)
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
            type = TypeResolver.resolveType(type, typeVariableContext);
            if (type instanceof ParameterizedType parameterizedType)
            {
                Class rawType = (Class) parameterizedType.getRawType();
                componentType = rawType;
            }
            else if (type instanceof Class ckazz)
            {
                componentType = ckazz;
            }
            else
            {
                throw new IllegalStateException("代码不应该运行到这里");
            }
            for (int i = 0; i < dim - 1; i++)
            {
                componentType = Array.newInstance(componentType, 0).getClass();
            }
            componentReader = dsonContext.parseReader(((GenericArrayType) origin).getGenericComponentType(), typeVariableContext);
        }
        else if (type instanceof Class ckazz)
        {
            componentType   = ckazz.getComponentType();
            componentReader = dsonContext.parseReader(componentType, typeVariableContext);
        }
        else
        {
            throw new IllegalArgumentException(type.toString());
        }
    }

    @Override
    public Object fromString(Stream stream, Map<TypeVariable<?>, Type> typeVariableContext)
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
                array[count] = componentReader.fromString(stream, typeVariableContext);
                count += 1;
            }
            stream.skipComma();
        }
        return Arrays.copyOf(array, count);
    }
}
