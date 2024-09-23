package com.jfirer.dson.reader.impl;

import com.jfirer.baseutil.reflect.ReflectUtil;
import com.jfirer.dson.reader.JsonReader;
import com.jfirer.dson.reader.Stream;
import com.jfirer.dson.reader.TypeReader;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class CollectionReader implements TypeReader
{
    Class      ckass;
    TypeReader typeReader;
    Type       type;
    private JsonReader jsonReader;

    @Override
    public void init(Type type, JsonReader jsonReader)
    {
        this.jsonReader = jsonReader;
        this.type       = type;
        if (type instanceof Class)
        {
            ckass = (Class) type;
        }
        else if (type instanceof ParameterizedType)
        {
            ckass = (Class) ((ParameterizedType) type).getRawType();
        }
        else
        {
            throw new IllegalArgumentException(type.toString());
        }
        if (ckass.isInterface())
        {
            if (List.class.isAssignableFrom(ckass))
            {
                ckass = ArrayList.class;
            }
            else if (Set.class.isAssignableFrom(ckass))
            {
                ckass = HashSet.class;
            }
            else if (Queue.class.isAssignableFrom(ckass))
            {
                ckass = LinkedList.class;
            }
            else if (Collection.class == ckass)
            {
                ckass = HashSet.class;
            }
            else
            {
                throw new IllegalArgumentException(ckass.toString());
            }
        }
    }

    @Override
    public Object fromString(Stream stream)
    {
        try
        {
            Collection collection = (Collection) ckass.newInstance();
            TypeReader typeReader = this.typeReader;
            if (typeReader == null)
            {
                if (type instanceof Class)
                {
                    ckass           = (Class) type;
                    this.typeReader = typeReader = jsonReader.parse(Object.class);
                }
                else if (type instanceof ParameterizedType)
                {
                    Type actualTypeArgument = ((ParameterizedType) type).getActualTypeArguments()[0];
                    typeReader      = jsonReader.parse(actualTypeArgument);
                    this.typeReader = typeReader;
                }
            }
            stream.startParseArray();
            while (stream.parseArrayEnd() == false)
            {
                if (stream.isNextNullAndSkip())
                {
                    ;
                }
                else
                {
                    collection.add(typeReader.fromString(stream));
                }
                stream.skipComma();
            }
            return collection;
        }
        catch (Exception e)
        {
            ReflectUtil.throwException(e);
            return null;
        }
    }
}
