package com.jfirer.dson.reader;

import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;

public class JsonReader
{
    private ConcurrentHashMap<Type, TypeReader> readers = new ConcurrentHashMap<Type, TypeReader>();

    public TypeReader get(Type type)
    {
        TypeReader typeReader = readers.get(type);
        if (typeReader != null)
        {
            return typeReader;
        }
        else
        {
            if (type instanceof Class)
            {
                typeReader = new ObjectReader();
                typeReader.init(type, this);
                readers.put(type, typeReader);
                return typeReader;
            }
            else
            {
                return null;
            }
        }
    }
}
