package com.jfirer.dson;

import com.jfirer.dson.reader.Stream;
import com.jfirer.dson.reader.TypeReader;

import java.lang.reflect.Type;
import java.util.function.BiConsumer;

public class Dson
{
    private static final ThreadLocal<StringBuilder> LOCAL           = ThreadLocal.withInitial(() -> new StringBuilder());
    private static final DsonContext                STANDARD_WRITER = new DsonContext();
    private static final DsonContext                COMPILE_WRITER  = new DsonContext(new DsonConfig().setWriteUseCompile(true));
    private static final DsonContext                STANDARD_READER = new DsonContext();
    private static final DsonContext                COMPILE_READER  = new DsonContext(new DsonConfig().setReadUseCompile(true));

    public static String toJson(Object entity)
    {
        return toJson(entity, (Object obj, StringBuilder builder) -> STANDARD_WRITER.parseWriter(obj.getClass()).toJson(obj, builder));
    }

    public static String toJsonByCompile(Object entity)
    {
        return toJson(entity, (Object obj, StringBuilder builder) -> COMPILE_WRITER.parseWriter(obj.getClass()).toJson(obj, builder));
    }

    private static String toJson(Object entity, BiConsumer<Object, StringBuilder> consumer)
    {
        StringBuilder output = LOCAL.get();
        consumer.accept(entity, output);
        String result = output.toString();
        output.setLength(0);
        return result;
    }

    public static <T> T fromString(Type type, String str)
    {
        TypeReader typeReader = STANDARD_READER.parseReader(type);
        return (T) typeReader.fromString(new Stream(str));
    }

    public static <T> T fromStringByCompile(Type type, String str)
    {
        return (T) COMPILE_READER.parseReader(type).fromString(new Stream(str));
    }

    public static Object fromString(String str)
    {
        return STANDARD_READER.parseReader(Object.class).fromString(new Stream(str));
    }

    public static Object fromStringByAttribute(String attribute, Type type, String str)
    {
        TypeReader typeReader = STANDARD_READER.parseReader(type);
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
