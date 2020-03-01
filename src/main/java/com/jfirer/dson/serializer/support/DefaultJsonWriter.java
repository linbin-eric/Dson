package com.jfirer.dson.serializer.support;

import com.jfirer.dson.serializer.JsonWriter;
import com.jfirer.dson.serializer.TypeWriter;
import com.jfirer.dson.serializer.buildin.*;
import com.jfirer.dson.serializer.impl.*;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultJsonWriter implements JsonWriter
{
    private ConcurrentHashMap<Type, TypeWriter> store = new ConcurrentHashMap<Type, TypeWriter>(256);

    public DefaultJsonWriter()
    {
        store.put(Integer.class, new IntegerWriter());
        store.put(Short.class, new ShortWriter());
        store.put(Long.class, new LongWriter());
        store.put(Float.class, new FloatWriter());
        store.put(Double.class, new DoubleWriter());
        store.put(Byte.class, new ByteWriter());
        store.put(Boolean.class, new BooleanWriter());
        store.put(Character.class, new CharWriter());
        store.put(String.class, new StringWriter());
        store.put(Date.class, new DateWriter());
        store.put(java.sql.Date.class, new DateWriter());
        store.put(int[].class, new ArrayWriter(this, int[].class));
        store.put(short[].class, new ArrayWriter(this, short[].class));
        store.put(long[].class, new ArrayWriter(this, long[].class));
        store.put(byte[].class, new ArrayWriter(this, byte[].class));
        store.put(float[].class, new ArrayWriter(this, float[].class));
        store.put(double[].class, new ArrayWriter(this, double[].class));
        store.put(boolean[].class, new ArrayWriter(this, boolean[].class));
        store.put(char[].class, new ArrayWriter(this, char[].class));
        store.put(String[].class, new ArrayWriter(this, String[].class));
    }

    @Override
    public TypeWriter get(Type type)
    {
        TypeWriter typeWriter = store.get(type);
        if (typeWriter == null)
        {
            if (type instanceof GenericArrayType)
            {
                typeWriter = new ArrayWriter();
            }
            else
            {
                Class targetClass;
                if (type instanceof ParameterizedType)
                {
                    targetClass = (Class<?>) ((ParameterizedType) type).getRawType();
                }
                else if (type instanceof Class<?>)
                {
                    targetClass = (Class) type;
                }
                else
                {
                    throw new IllegalArgumentException("当前类型:" + type);
                }
                if (targetClass.isArray())
                {
                    typeWriter = new ArrayWriter();
                }
                else if (Map.class.isAssignableFrom(targetClass))
                {
                    typeWriter = new MapWriter();
                }
                else if (ArrayList.class.isAssignableFrom(targetClass))
                {
                    typeWriter = new ArrayListWriter();
                }
                else if (Collection.class.isAssignableFrom(targetClass))
                {
                    typeWriter = new CollectionWriter();
                }
                else if (Enum.class.isAssignableFrom(targetClass))
                {
                    typeWriter = new EnumWriter();
                }
                else
                {
                    typeWriter = new CompileObjectWriter();
                }
            }
            typeWriter.initialize(this, type);
            store.put(type, typeWriter);
        }
        return typeWriter;
    }

    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        if (entity == null)
        {
            return;
        }
        TypeWriter serializeDescriptor = get(entity.getClass());
        serializeDescriptor.toJson(entity, output);
    }
}
