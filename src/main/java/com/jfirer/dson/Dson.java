package com.jfirer.dson;

import com.jfirer.dson.reader.JsonReader;
import com.jfirer.dson.reader.Stream;
import com.jfirer.dson.reader.TypeReader;
import com.jfirer.dson.writer.JsonWriter;

import java.lang.reflect.Type;

public class Dson
{

    private static final ThreadLocal<StringBuilder> LOCAL      = new ThreadLocal<StringBuilder>()
    {
        protected StringBuilder initialValue()
        {
            return new StringBuilder();
        }
    };
    private static       JsonWriter                 jsonWriter = new JsonWriter();
    private static       JsonReader                 jsonReader = new JsonReader();

    public static String toJsonString(Object entity)
    {
        StringBuilder output = LOCAL.get();
        jsonWriter.toJson(entity, output);
        String result = output.toString();
        output.setLength(0);
        return result;
    }

    public static <T> T fromString(Type type, String str)
    {
        TypeReader typeReader = jsonReader.get(type);
        return (T) typeReader.fromString(new Stream(str));
    }

    public static Object fromString(String str)
    {
        return jsonReader.get(Object.class).fromString(new Stream(str));
    }

    public static TypeReader get(Type type)
    {
        return jsonReader.get(type);
    }
}
