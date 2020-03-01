package com.jfirer.dson.deserializer.impl;

import com.jfirer.dson.deserializer.DeserializeDescriptor;
import com.jfirer.dson.deserializer.Deserializer;
import com.jfirer.dson.metadata.parse.Lexer;
import com.jfirer.baseutil.reflect.ReflectUtil;
import com.jfirer.dson.metadata.json.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Map;

public class MapDeserializeDescriptor implements DeserializeDescriptor
{
    private DeserializeDescriptor keyDescriber;
    private DeserializeDescriptor valueDescriber;
    private Class<?>              instanceType;

    @Override
    public void initialize(Type type, Deserializer deserializer, Map<Type, DeserializeDescriptor> map)
    {
        if (type instanceof ParameterizedType)
        {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            Type   keyType             = actualTypeArguments[0];
            if (keyType instanceof WildcardType)
            {
                keyDescriber = deserializer.describe(Object.class, map);
            }
            else if (keyType instanceof Class<?> || keyType instanceof ParameterizedType)
            {
                keyDescriber = deserializer.describe(keyType, map);
            }
            else
            {
                throw new IllegalArgumentException("未能支持Map的key是类型:" + keyType);
            }
            Type valueType = actualTypeArguments[1];
            if (valueType instanceof WildcardType)
            {
                valueDescriber = deserializer.describe(Object.class, map);
            }
            else if (valueType instanceof Class || valueType instanceof ParameterizedType)
            {
                valueDescriber = deserializer.describe(valueType, map);
            }
            else
            {
                throw new IllegalArgumentException("未能支持Map的Value类型:" + valueType);
            }
            instanceType = (Class<?>) ((ParameterizedType) type).getRawType();
        }
        else if (type instanceof Class)
        {
            keyDescriber = deserializer.describe(String.class, map);
            valueDescriber = deserializer.describe(Object.class, map);
            instanceType = (Class<?>) type;
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object deserialize(DsonObject dsonObject)
    {
        JsonCollection collection = (JsonCollection) dsonObject;
        try
        {
            Map<Object, Object> instance = (Map<Object, Object>) instanceType.newInstance();
            for (Entry entry : collection.getEntries())
            {
                if (entry.getValueType() == JsonValueType.NULL)
                {
                    continue;
                }
                Object key   = keyDescriber.deserialize(entry.getName());
                Object value = valueDescriber.deserialize(entry);
                instance.put(key, value);
            }
            return instance;
        }
        catch (Exception e)
        {
            ReflectUtil.throwException(e);
            return null;
        }
    }

    @Override
    public Object deserialize(String json)
    {
        return deserialize(new Lexer(json).parse());
    }

    @Override
    public Object deserialize(Entry entry)
    {
        if (entry.getValueType() != JsonValueType.COLLECTION)
        {
            throw new IllegalArgumentException();
        }
        return deserialize((JsonCollection) entry.getValue());
    }

    @Override
    public Object deserialize(Element element)
    {
        if (element.getValueType() != JsonValueType.COLLECTION)
        {
            throw new IllegalArgumentException();
        }
        return deserialize((JsonCollection) element.getValue());
    }
}
