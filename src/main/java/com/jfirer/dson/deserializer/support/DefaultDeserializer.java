package com.jfirer.dson.deserializer.support;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.jfirer.dson.deserializer.DeserializeDescriptor;
import com.jfirer.dson.deserializer.Deserializer;
import com.jfirer.dson.deserializer.buildin.BooleanDeserializeDescriptor;
import com.jfirer.dson.deserializer.buildin.ByteDeserializeDescriptor;
import com.jfirer.dson.deserializer.buildin.CharacterDeserializeDescriptor;
import com.jfirer.dson.deserializer.buildin.DateDeserializeDescriptor;
import com.jfirer.dson.deserializer.buildin.DoubleDeserializeDescriptor;
import com.jfirer.dson.deserializer.buildin.FloatDeserializeDescriptor;
import com.jfirer.dson.deserializer.buildin.IntegerDeserializeDescriptor;
import com.jfirer.dson.deserializer.buildin.LongDeserializeDescriptor;
import com.jfirer.dson.deserializer.buildin.ObjectDeserializeDescriptor;
import com.jfirer.dson.deserializer.buildin.ShortDeserializeDescriptor;
import com.jfirer.dson.deserializer.buildin.StringDeserializDescriptor;
import com.jfirer.dson.deserializer.buildin.array.BooleanArrayDeserializeDescriptor;
import com.jfirer.dson.deserializer.buildin.array.ByteArrayDeserializeDescriptor;
import com.jfirer.dson.deserializer.buildin.array.CharArrayDeserializeDescriptor;
import com.jfirer.dson.deserializer.buildin.array.DoubleArrayDeserializeDescriptor;
import com.jfirer.dson.deserializer.buildin.array.FloatArrayDeserializeDescriptor;
import com.jfirer.dson.deserializer.buildin.array.IntArrayDeserializeDescriptor;
import com.jfirer.dson.deserializer.buildin.array.LongArrayDeserializeDescriptor;
import com.jfirer.dson.deserializer.buildin.array.ShortArrayDeserializeDescriptor;
import com.jfirer.dson.deserializer.buildin.array.StringArrayDeserializeDescriptor;
import com.jfirer.dson.deserializer.impl.ArrayDeserializeDescriptor;
import com.jfirer.dson.deserializer.impl.CollectionDeserializeDecriptor;
import com.jfirer.dson.deserializer.impl.EnumDeserializeDescriptor;
import com.jfirer.dson.deserializer.impl.MapDeserializeDescriptor;
import com.jfirer.dson.deserializer.impl.ReflectBeanDeserializeDescriptor;

public class DefaultDeserializer implements Deserializer
{
    private ConcurrentMap<Type, DeserializeDescriptor> store = new ConcurrentHashMap<Type, DeserializeDescriptor>();
    
    public DefaultDeserializer()
    {
        store.put(Character.class, new CharacterDeserializeDescriptor());
        store.put(Byte.class, new ByteDeserializeDescriptor());
        store.put(Integer.class, new IntegerDeserializeDescriptor());
        store.put(Short.class, new ShortDeserializeDescriptor());
        store.put(Long.class, new LongDeserializeDescriptor());
        store.put(Float.class, new FloatDeserializeDescriptor());
        store.put(Double.class, new DoubleDeserializeDescriptor());
        store.put(Boolean.class, new BooleanDeserializeDescriptor());
        store.put(Object.class, new ObjectDeserializeDescriptor());
        store.put(String.class, new StringDeserializDescriptor());
        store.put(Date.class, new DateDeserializeDescriptor());
        store.put(java.sql.Date.class, new DateDeserializeDescriptor());
        //
        store.put(boolean[].class, new BooleanArrayDeserializeDescriptor());
        store.put(byte[].class, new ByteArrayDeserializeDescriptor());
        store.put(char[].class, new CharArrayDeserializeDescriptor());
        store.put(double[].class, new DoubleArrayDeserializeDescriptor());
        store.put(float[].class, new FloatArrayDeserializeDescriptor());
        store.put(int[].class, new IntArrayDeserializeDescriptor());
        store.put(long[].class, new LongArrayDeserializeDescriptor());
        store.put(short[].class, new ShortArrayDeserializeDescriptor());
        store.put(String[].class, new StringArrayDeserializeDescriptor());
    }
    
    @Override
    public DeserializeDescriptor describe(Type type, Map<Type, DeserializeDescriptor> map)
    {
        if (map.containsKey(type))
        {
            return map.get(type);
        }
        DeserializeDescriptor describer = store.get(type);
        if (describer == null)
        {
            if (type instanceof ParameterizedType)
            {
                Class<?> rawType = (Class<?>) ((ParameterizedType) type).getRawType();
                if (Map.class.isAssignableFrom(rawType))
                {
                    describer = new MapDeserializeDescriptor();
                }
                else if (Collection.class.isAssignableFrom(rawType))
                {
                    describer = new CollectionDeserializeDecriptor();
                }
                else if (Enum.class.isAssignableFrom(rawType))
                {
                    describer = new EnumDeserializeDescriptor();
                }
                else
                {
                    describer = new ReflectBeanDeserializeDescriptor();
                }
            }
            else if (type instanceof Class<?>)
            {
                if (Map.class.isAssignableFrom((Class<?>) type))
                {
                    describer = new MapDeserializeDescriptor();
                }
                else if (Collection.class.isAssignableFrom((Class<?>) type))
                {
                    describer = new CollectionDeserializeDecriptor();
                }
                else if (Enum.class.isAssignableFrom((Class<?>) type))
                {
                    describer = new EnumDeserializeDescriptor();
                }
                else if (((Class<?>) type).isArray())
                {
                    describer = new ArrayDeserializeDescriptor();
                }
                else
                {
                    describer = new ReflectBeanDeserializeDescriptor();
                }
            }
            else if (type instanceof GenericArrayType)
            {
                describer = new ArrayDeserializeDescriptor();
            }
            else
            {
                throw new IllegalArgumentException("非法参数:" + type);
            }
            map.put(type, describer);
            describer.initialize(type, this, map);
        }
        return describer;
    }
    
    @Override
    public DeserializeDescriptor describe(Type type)
    {
        DeserializeDescriptor describer = store.get(type);
        if (describer == null)
        {
            Map<Type, DeserializeDescriptor> map = new HashMap<Type, DeserializeDescriptor>();
            describer = describe(type, map);
            store.putIfAbsent(type, describer);
        }
        return describer;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(Type type, String json)
    {
        DeserializeDescriptor deserializeDescriber = describe(type);
        return (T) deserializeDescriber.deserialize(json);
    }
    
}
