package com.jfirer.dson;

import com.jfirer.dson.deserializer.Deserializer;
import com.jfirer.dson.deserializer.support.DefaultDeserializer;
import com.jfirer.dson.reader.JsonReader;
import com.jfirer.dson.reader.Stream;
import com.jfirer.dson.reader.TypeReader;
import com.jfirer.dson.serializer.JsonWriter;
import com.jfirer.dson.serializer.support.DefaultJsonWriter;

import java.lang.reflect.Type;

public class Dson
{

    private static       Deserializer               deserializer = new DefaultDeserializer();
    private static       JsonWriter                 jsonWriter   = new DefaultJsonWriter();
    private static       JsonReader                 jsonReader   = new JsonReader();
    private static final ThreadLocal<StringBuilder> LOCAL        = new ThreadLocal<StringBuilder>()
    {
        protected StringBuilder initialValue()
        {
            return new StringBuilder();
        }
    };

    public static String toJsonString(Object entity)
    {
        StringBuilder output = LOCAL.get();
        jsonWriter.toJson(entity, output);
        String result = output.toString();
        output.setLength(0);
        return result;
    }

    public static <T> T fromString2(Type type, String json)
    {
        return deserializer.deserialize(type, json);
    }

    public static <T> T fromString(Type type, String str)
    {
        TypeReader typeReader = jsonReader.get(type);
        return (T) typeReader.fromString(new Stream(str));
    }

    public static TypeReader get(Type type)
    {
        return jsonReader.get(type);
    }
}
