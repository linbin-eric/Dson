package com.jfirer.dson;

import com.jfirer.dson.reader.JsonReader;
import com.jfirer.dson.reader.Stream;
import com.jfirer.dson.reader.TypeReader;
import com.jfirer.dson.writer.JsonWriter;

import java.lang.reflect.Type;
import java.util.function.BiConsumer;

public class Dson
{

    private static final ThreadLocal<StringBuilder> LOCAL               = ThreadLocal.withInitial(() -> new StringBuilder());
    private static final JsonWriter                 JSONWRITER          = new JsonWriter();
    private static final JsonWriter                 JSON_WRITER_COMPILE = new JsonWriter(true);
    private static final JsonReader                 JSONREADER          = new JsonReader();
    private static final JsonReader                 JSON_READER_COMPILE = new JsonReader(true);

    public static String toJson(Object entity)
    {
        return toJson(entity, (Object obj, StringBuilder builder) -> JSONWRITER.toJson(obj, builder));
    }

    public static String toJsonByCompile(Object entity)
    {
        return toJson(entity, (Object obj, StringBuilder builder) -> JSON_WRITER_COMPILE.toJson(obj, builder));
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
        TypeReader typeReader = JSONREADER.get(type);
        return (T) typeReader.fromString(new Stream(str));
    }

    public static <T> T fromStringByCompile(Type type, String str)
    {
        return (T) JSON_READER_COMPILE.get(type).fromString(new Stream(str));
    }

    public static Object fromString(String str)
    {
        return JSONREADER.get(Object.class).fromString(new Stream(str));
    }

    public static TypeReader get(Type type)
    {
        return JSONREADER.get(type);
    }
}
