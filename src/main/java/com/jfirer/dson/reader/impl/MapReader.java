package com.jfirer.dson.reader.impl;

import com.jfirer.baseutil.reflect.ReflectUtil;
import com.jfirer.dson.reader.JsonReader;
import com.jfirer.dson.reader.Stream;
import com.jfirer.dson.reader.TypeReader;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MapReader implements TypeReader
{
    private TypeReader valueReader;
    private JsonReader jsonReader;
    private Type       type;
    private Class      ckass;

    @Override
    public void init(Type type, JsonReader jsonReader)
    {
        this.type       = type;
        this.jsonReader = jsonReader;
        if (type instanceof Class)
        {
            ckass = (Class) type;
        }
        else if (type instanceof ParameterizedType)
        {
            ckass = (Class) ((ParameterizedType) type).getRawType();
            Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
            if (typeArguments[0] != String.class)
            {
                throw new IllegalArgumentException(type.toString());
            }
        }
        else
        {
            throw new IllegalArgumentException(type.toString());
        }
        if (ckass.isInterface())
        {
            ckass = HashMap.class;
        }
    }

    @Override
    public Object fromString(Stream stream)
    {
        TypeReader valueReader = this.valueReader;
        if (valueReader == null)
        {
            if (type instanceof Class)
            {
                this.valueReader = valueReader = jsonReader.parse(Object.class);
            }
            else if (type instanceof ParameterizedType)
            {
                Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
                this.valueReader = valueReader = jsonReader.parse(typeArguments[1]);
            }
        }
        stream.startParseObject();
        try
        {
            Map map = (Map) ckass.newInstance();
            while (stream.parseObjectEnd() == false)
            {
                String name = stream.getName();
                stream.skipColon();
                if (stream.isNextNullAndSkip())
                {
                    ;
                }
                else
                {
                    map.put(name, valueReader.fromString(stream));
                }
                stream.skipComma();
            }
            return map;
        }
        catch (Exception e)
        {
            ReflectUtil.throwException(e);
            return null;
        }
    }
}
