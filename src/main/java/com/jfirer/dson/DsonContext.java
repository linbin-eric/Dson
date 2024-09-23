package com.jfirer.dson;

import com.jfirer.dson.reader.TypeReader;
import com.jfirer.dson.reader.impl.*;
import com.jfirer.dson.reader.impl.basic.*;
import com.jfirer.dson.writer.TypeWriter;
import com.jfirer.dson.writer.impl.*;
import com.jfirer.dson.writer.impl.basic.*;
import lombok.Getter;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DsonContext
{
    private ConcurrentHashMap<Type, TypeReader> readers = new ConcurrentHashMap<Type, TypeReader>();
    private ConcurrentHashMap<Type, TypeWriter> writers = new ConcurrentHashMap<Type, TypeWriter>(256);
    @Getter
    private DsonConfig                          config;

    public DsonContext(DsonConfig config)
    {
        this.config = config;
        readers.put(String.class, new StringReader());
        readers.put(Integer.class, new IntegerReader());
        readers.put(Long.class, new LongReader());
        readers.put(Short.class, new ShortReader());
        readers.put(Byte.class, new ByteReader());
        readers.put(Character.class, new CharReader());
        readers.put(Boolean.class, new BooleanReader());
        readers.put(Float.class, new FloatReader());
        readers.put(Double.class, new DoubleReader());
        writers.put(Integer.class, new IntegerWriter());
        writers.put(Short.class, new ShortWriter());
        writers.put(Long.class, new LongWriter());
        writers.put(Float.class, new FloatWriter());
        writers.put(Double.class, new DoubleWriter());
        writers.put(Byte.class, new ByteWriter());
        writers.put(Boolean.class, new BooleanWriter());
        writers.put(Character.class, new CharWriter());
        writers.put(String.class, new StringWriter());
        writers.put(Date.class, new DateWriter());
        writers.put(java.sql.Date.class, new DateWriter());
        writers.put(int[].class, new ArrayWriter(this, int[].class));
        writers.put(short[].class, new ArrayWriter(this, short[].class));
        writers.put(long[].class, new ArrayWriter(this, long[].class));
        writers.put(byte[].class, new ArrayWriter(this, byte[].class));
        writers.put(float[].class, new ArrayWriter(this, float[].class));
        writers.put(double[].class, new ArrayWriter(this, double[].class));
        writers.put(boolean[].class, new ArrayWriter(this, boolean[].class));
        writers.put(char[].class, new ArrayWriter(this, char[].class));
        writers.put(String[].class, new ArrayWriter(this, String[].class));
    }

    public DsonContext()
    {
        this(DsonConfig.STANDARD);
    }

    public TypeReader parseReader(Type type)
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
                else if (Enum.class.isAssignableFrom(rawType))
                {
                    typeReader = new EnumNameReader();
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
            readers.put(type, typeReader);
            typeReader.init(type, this);
            return typeReader;
        }
    }

    public TypeWriter parseWriter(Type type)
    {
        TypeWriter typeWriter = writers.get(type);
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
                    if (config.isWriteUseCompile())
                    {
                        typeWriter = TypeWriter.compile(type);
                    }
                    else
                    {
                        typeWriter = TypeWriter.standard();
                    }
                }
            }
            writers.put(type, typeWriter);
            typeWriter.initialize(type, this);
        }
        return typeWriter;
    }
}
