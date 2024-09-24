package com.jfirer.dson;

import com.jfirer.dson.reader.DeSerializeDefinition;
import com.jfirer.dson.reader.Stream;
import com.jfirer.dson.reader.TypeReader;
import com.jfirer.dson.reader.impl.*;
import com.jfirer.dson.reader.impl.basic.*;
import com.jfirer.dson.reader.impl.basic.array.*;
import com.jfirer.dson.reader.impl.basic.array.boxed.*;
import com.jfirer.dson.writer.SerializeDefinition;
import com.jfirer.dson.writer.TypeWriter;
import com.jfirer.dson.writer.impl.*;
import com.jfirer.dson.writer.impl.basic.*;
import com.jfirer.dson.writer.impl.basic.array.*;
import com.jfirer.dson.writer.impl.basic.array.boxed.*;
import lombok.Getter;
import lombok.SneakyThrows;

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
    private static final ThreadLocal<StringBuilder>          LOCAL   = ThreadLocal.withInitial(() -> new StringBuilder());
    private              ConcurrentHashMap<Type, TypeReader> readers = new ConcurrentHashMap<Type, TypeReader>();
    private              ConcurrentHashMap<Type, TypeWriter> writers = new ConcurrentHashMap<Type, TypeWriter>(256);
    @Getter
    private              DsonConfig                          config;

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
        readers.put(int[].class, new IntArrayReader());
        readers.put(short[].class, new ShortArrayReader());
        readers.put(long[].class, new LongArrayReader());
        readers.put(byte[].class, new ByteArrayReader());
        readers.put(float[].class, new FloatArrayReader());
        readers.put(double[].class, new DoubleArrayReader());
        readers.put(boolean[].class, new BooleanArrayReader());
        readers.put(char[].class, new CharArrayReader());
        readers.put(String[].class, new StringArrayReader());
        readers.put(Integer[].class, new ClassIntArrayReader());
        readers.put(Long[].class, new ClassLongArrayReader());
        readers.put(Float[].class, new ClassFloatArrayReader());
        readers.put(Double[].class, new ClassDoubleArrayReader());
        readers.put(Boolean[].class, new ClassBooleanArrayReader());
        readers.put(Character[].class, new ClassCharArrayReader());
        readers.put(Short[].class, new ClassShortArrayReader());
        readers.put(Byte[].class, new ClassByteArrayReader());
        ////
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
        writers.put(int[].class, new IntArrayWriter());
        writers.put(long[].class, new LongArrayWriter());
        writers.put(float[].class, new FloatArrayWriter());
        writers.put(double[].class, new DoubleArrayWriter());
        writers.put(boolean[].class, new BooleanArrayWriter());
        writers.put(char[].class, new CharArrayWriter());
        writers.put(byte[].class, new ByteArrayWriter());
        writers.put(short[].class, new ShortArrayWriter());
        writers.put(String[].class, new StringArrayWriter());
        writers.put(Integer[].class, new ClassIntArrayWriter());
        writers.put(Long[].class, new ClassLongArrayWriter());
        writers.put(Float[].class, new ClassFloatArrayWriter());
        writers.put(Double[].class, new ClassDoubleArrayWriter());
        writers.put(Boolean[].class, new ClassBooleanArrayWriter());
        writers.put(Character[].class, new ClassCharArrayWriter());
        writers.put(Short[].class, new ClassShortArrayWriter());
        writers.put(Byte[].class, new ClassByteArrayWriter());
    }

    public DsonContext()
    {
        this(DsonConfig.STANDARD);
    }

    @SneakyThrows
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
                typeReader = new NewArrayReader();
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
                if (rawType.isAnnotationPresent(DeSerializeDefinition.class))
                {
                    typeReader = ((DeSerializeDefinition) rawType.getAnnotation(DeSerializeDefinition.class)).value().getConstructor().newInstance();
                }
                else if (rawType.isArray())
                {
                    typeReader = new NewArrayReader();
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
                    typeReader = config.isReadUseCompile() ? TypeReader.compile(rawType) : new ObjectReader();
                }
            }
            readers.put(type, typeReader);
            typeReader.init(type, this);
            return typeReader;
        }
    }

    @SneakyThrows
    public TypeWriter parseWriter(Type type)
    {
        TypeWriter typeWriter = writers.get(type);
        if (typeWriter == null)
        {
            if (type instanceof GenericArrayType)
            {
                typeWriter = ArrayWriter.findSuitableArrayWriter(type);
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
                if (targetClass.isAnnotationPresent(SerializeDefinition.class))
                {
                    SerializeDefinition annotation = (SerializeDefinition) targetClass.getAnnotation(SerializeDefinition.class);
                    typeWriter = annotation.value().getConstructor().newInstance();
                }
                else if (targetClass.isArray())
                {
                    typeWriter = ArrayWriter.findSuitableArrayWriter(type);
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

    public <T> T fromString(Type type, String str)
    {
        TypeReader typeReader = parseReader(type);
        return (T) typeReader.fromString(new Stream(str));
    }

    public String toJson(Object entity)
    {
        StringBuilder output     = LOCAL.get();
        TypeWriter    typeWriter = parseWriter(entity.getClass());
        typeWriter.toJson(entity, output);
        String result = output.toString();
        output.setLength(0);
        return result;
    }

    public Object fromStringByAttribute(String attribute, Type type, String str)
    {
        TypeReader typeReader = parseReader(type);
        Stream     stream     = new Stream(str);
        stream.startParseObject();
        boolean skipComma = false;
        while (skipComma || stream.parseObjectEnd() == false)
        {
            String name = stream.getName();
            stream.skipColon();
            if (name.equals(attribute))
            {
                return typeReader.fromString(stream);
            }
            else
            {
                stream.skipWholeValue();
            }
            skipComma = stream.skipComma();
        }
        return null;
    }
}
