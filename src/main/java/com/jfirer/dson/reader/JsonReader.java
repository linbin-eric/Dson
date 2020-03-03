package com.jfirer.dson.reader;

import com.jfirer.dson.reader.buildin.*;
import com.jfirer.dson.reader.impl.*;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JsonReader
{
    private ConcurrentHashMap<Type, TypeReader> readers = new ConcurrentHashMap<Type, TypeReader>();

    public JsonReader()
    {
        readers.put(String.class, new StringReader());
        readers.put(Integer.class, new IntegerReader());
        readers.put(Long.class, new LongReader());
        readers.put(Short.class, new ShortReader());
        readers.put(Byte.class, new ByteReader());
        readers.put(Character.class, new CharReader());
        readers.put(Boolean.class, new BooleanReader());
        readers.put(Float.class, new FloatReader());
        readers.put(Double.class, new DoubleReader());
    }

    public TypeReader get(Type type)
    {
        TypeReader typeReader = readers.get(type);
        if (typeReader != null)
        {
            return typeReader;
        }
        else
        {
            if (type instanceof GenericArrayType)
            {
                typeReader = new ArrayReader();
            }
            else
            {
                Class rawType = null;
                if (type instanceof ParameterizedType)
                {
                    rawType = (Class) ((ParameterizedType) type).getRawType();
                }
                else if (type instanceof Class)
                {
                    rawType = (Class) type;
                }
                else
                {
                    throw new IllegalArgumentException(type.toString());
                }
                if (rawType.isArray())
                {
                    typeReader = new ArrayReader();
                }
                else if (Collection.class.isAssignableFrom(rawType))
                {
                    typeReader = new CollectionReader();
                }
                else if (Map.class.isAssignableFrom(rawType))
                {
                    typeReader = new MapReader();
                }
                else if (rawType == Object.class)
                {
                    typeReader = new UnKnowTypeReader();
                }
                else
                {
                    typeReader = new ObjectReader();
                }
            }
            typeReader.init(type, this);
            readers.put(type, typeReader);
            return typeReader;
        }
    }
}
