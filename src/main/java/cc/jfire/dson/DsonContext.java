package cc.jfire.dson;

import cc.jfire.dson.reader.DeSerializeDefinition;
import cc.jfire.dson.reader.ReaderContext;
import cc.jfire.dson.reader.Stream;
import cc.jfire.dson.reader.TypeReader;
import cc.jfire.dson.reader.impl.*;
import cc.jfire.dson.reader.impl.basic.*;
import cc.jfire.dson.reader.impl.basic.array.*;
import cc.jfire.dson.reader.impl.basic.array.boxed.*;
import cc.jfire.dson.writer.SerializeDefinition;
import cc.jfire.dson.writer.TypeWriter;
import cc.jfire.dson.writer.impl.*;
import cc.jfire.dson.writer.impl.basic.*;
import cc.jfire.dson.writer.impl.basic.array.*;
import cc.jfire.dson.writer.impl.basic.array.boxed.*;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DsonContext
{
    private final        ConcurrentHashMap<Type, ReaderContext> readerContexts      = new ConcurrentHashMap<Type, ReaderContext>();
    private final        Map<Type, TypeReader>                  standardReaders     = new ConcurrentHashMap<Type, TypeReader>();
    private final        ConcurrentHashMap<Type, TypeWriter>    writers             = new ConcurrentHashMap<Type, TypeWriter>(256);
    @Getter
    private              DsonConfig                             config;
    private static final ThreadLocal<Map<Type, TypeWriter>>     CURRENT_WRITER_CACHE = ThreadLocal.withInitial(() -> new HashMap<>());




    public DsonContext(DsonConfig config)
    {
        this.config = config;
        standardReaders.put(String.class, new StringReader());
        standardReaders.put(Integer.class, new IntegerReader());
        standardReaders.put(Long.class, new LongReader());
        standardReaders.put(Short.class, new ShortReader());
        standardReaders.put(Byte.class, new ByteReader());
        standardReaders.put(Character.class, new CharReader());
        standardReaders.put(Boolean.class, new BooleanReader());
        standardReaders.put(Float.class, new FloatReader());
        standardReaders.put(Double.class, new DoubleReader());
        standardReaders.put(int[].class, new IntArrayReader());
        standardReaders.put(short[].class, new ShortArrayReader());
        standardReaders.put(long[].class, new LongArrayReader());
        standardReaders.put(byte[].class, new ByteArrayReader());
        standardReaders.put(float[].class, new FloatArrayReader());
        standardReaders.put(double[].class, new DoubleArrayReader());
        standardReaders.put(boolean[].class, new BooleanArrayReader());
        standardReaders.put(char[].class, new CharArrayReader());
        standardReaders.put(String[].class, new StringArrayReader());
        standardReaders.put(Integer[].class, new ClassIntArrayReader());
        standardReaders.put(Long[].class, new ClassLongArrayReader());
        standardReaders.put(Float[].class, new ClassFloatArrayReader());
        standardReaders.put(Double[].class, new ClassDoubleArrayReader());
        standardReaders.put(Boolean[].class, new ClassBooleanArrayReader());
        standardReaders.put(Character[].class, new ClassCharArrayReader());
        standardReaders.put(Short[].class, new ClassShortArrayReader());
        standardReaders.put(Byte[].class, new ClassByteArrayReader());
        standardReaders.put(BigDecimal.class, new BigDecimalReader());
        standardReaders.put(LocalDateTime.class, new LocalDateTimeReader());
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
        writers.put(BigDecimal.class, new BigDecimalWriter());
        writers.put(LocalDateTime.class, new LocalDateTimeWriter());
    }

    public DsonContext()
    {
        this(DsonConfig.STANDARD);
    }

    public ReaderContext getReaderContext(Type type)
    {
        return readerContexts.computeIfAbsent(type, each -> new ReaderContext(each, this, standardReaders));
    }

    public TypeReader parseReader(Type type)
    {
        return getReaderContext(type).getRootReader();
    }

    @Deprecated
    public TypeReader parseReader(Type type, Map<TypeVariable<?>, Type> ignored)
    {
        return parseReader(type);
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
        ReaderContext readerContext = getReaderContext(type);
        return (T) readerContext.getRootReader().fromString(new Stream(str));
    }

    @Deprecated
    public <T> T fromString(Type type, String str, Map<TypeVariable<?>, Type> ignored)
    {
        return fromString(type, str);
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

    public Object toJsonValue(Object entity)
    {
        if (entity == null)
        {
            return null;
        }
        TypeWriter typeWriter = parseWriter(entity.getClass());
        return typeWriter.toJsonValue(entity);
    }

    public Object fromStringByAttribute(String attribute, Type type, String str)
    {
        TypeReader typeReader = getReaderContext(type).getRootReader();
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

    @Deprecated
    public Object fromStringByAttribute(String attribute, Type type, String str, Map<TypeVariable<?>, Type> ignored)
    {
        return fromStringByAttribute(attribute, type, str);
    }
}
