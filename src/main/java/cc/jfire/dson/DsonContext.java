package cc.jfire.dson;

import cc.jfire.dson.reader.DeSerializeDefinition;
import cc.jfire.dson.reader.Stream;
import cc.jfire.dson.reader.TypeReader;
import cc.jfire.dson.reader.impl.*;
import cc.jfire.dson.reader.impl.basic.*;
import cc.jfire.dson.reader.impl.basic.array.*;
import cc.jfire.dson.reader.impl.basic.array.boxed.*;
import cc.jfire.dson.writer.impl.*;
import cc.jfire.dson.writer.impl.basic.*;
import cc.jfire.dson.writer.impl.basic.array.*;
import cc.jfire.dson.writer.impl.basic.array.boxed.*;
import cc.jfire.dson.writer.SerializeDefinition;
import cc.jfire.dson.writer.TypeWriter;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DsonContext
{
    private              ConcurrentHashMap<Type, TypeReader> readers              = new ConcurrentHashMap<Type, TypeReader>();
    private              ConcurrentHashMap<Type, TypeWriter> writers              = new ConcurrentHashMap<Type, TypeWriter>(256);
    @Getter
    private              DsonConfig                          config;
    private static final ThreadLocal<Map<Type, TypeWriter>>  CURRENT_WRITER_CACHE = ThreadLocal.withInitial(() -> new HashMap<>());
    private static final ThreadLocal<Map<Type, TypeReader>>  CURRENT_READER_CACHE = ThreadLocal.withInitial(() -> new HashMap<>());

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
        readers.put(BigDecimal.class,new BigDecimalReader());
        readers.put(LocalDateTime.class, new LocalDateTimeReader());
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
        writers.put(BigDecimal.class,new BigDecimalWriter());
        writers.put(LocalDateTime.class, new LocalDateTimeWriter());
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
        Map<Type, TypeReader> cache = CURRENT_READER_CACHE.get();
        TypeReader            tmp   = cache.get(type);
        if (tmp != null)
        {
            return tmp;
        }
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
                try
                {
                    typeReader = ((DeSerializeDefinition) rawType.getAnnotation(DeSerializeDefinition.class)).value().getConstructor().newInstance();
                }
                catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
                {
                    throw new RuntimeException(e);
                }
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
            else if (rawType.isRecord())
            {
                typeReader = new ObjectReader();
            }
            else
            {
                typeReader = config.isReadUseCompile() ? TypeReader.compile(rawType) : new ObjectReader();
            }
        }
        cache.put(type, typeReader);
        typeReader.initialize(type, this);
        cache.remove(type);
        readers.put(type, typeReader);
        return typeReader;
    }

    @SneakyThrows
    public TypeWriter parseWriter(Type type)
    {
        TypeWriter typeWriter = writers.get(type);
        if (typeWriter != null)
        {
            return typeWriter;
        }
        Map<Type, TypeWriter> cache = CURRENT_WRITER_CACHE.get();
        TypeWriter            tmp   = cache.get(type);
        if (tmp != null)
        {
            return tmp;
        }
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
                try
                {
                    typeWriter = annotation.value().getConstructor().newInstance();
                }
                catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
                {
                    throw new RuntimeException(e);
                }
            }
            else if (targetClass.isArray())
            {
                typeWriter = ArrayWriter.findSuitableArrayWriter(targetClass);
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
            else if (targetClass.isRecord())
            {
                typeWriter = new ObjectWriter();
            }
            else
            {
                if (config.isWriteUseCompile())
                {
                    typeWriter = TypeWriter.compile((Class) type);
                }
                else
                {
                    typeWriter = TypeWriter.standard();
                }
            }
        }
        cache.put(type, typeWriter);
        typeWriter.initialize(type, this);
        cache.remove(type);
        writers.put(type, typeWriter);
        return typeWriter;
    }

    public <T> T fromString(Type type, String str)
    {
        TypeReader typeReader = parseReader(type);
        return (T) typeReader.fromString(new Stream(str));
    }

    public String toJson(Object entity)
    {
        StringBuilder output     = new StringBuilder();
        TypeWriter    typeWriter = parseWriter(entity.getClass());
        typeWriter.toJson(entity, output);
        String result = output.toString();
        output.setLength(0);
        return result;
    }

    public Object toJsonObject(Object entity)
    {
        if (entity == null)
        {
            return null;
        }
        TypeWriter typeWriter = parseWriter(entity.getClass());
        return typeWriter.toJsonObject(entity);
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
