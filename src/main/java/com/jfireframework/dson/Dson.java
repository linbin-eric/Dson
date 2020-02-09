package com.jfireframework.dson;

import com.jfireframework.dson.deserializer.Deserializer;
import com.jfireframework.dson.deserializer.support.DefaultDeserializer;
import com.jfireframework.dson.serializer.JsonWriter;
import com.jfireframework.dson.serializer.support.DefaultJsonWriter;

import java.lang.reflect.Type;

public class Dson
{

    private static       Deserializer               deserializer = new DefaultDeserializer();
    private static       JsonWriter                 jsonWriter   = new DefaultJsonWriter();
    private static final ThreadLocal<StringBuilder> LOCAL        = new ThreadLocal<StringBuilder>()
    {
        protected StringBuilder initialValue()
        {
            return new StringBuilder();
        }
    };

    public static String toJsonString(Object entity)
    {
        StringBuilder output     = LOCAL.get();
        jsonWriter.toJson(entity, output);
        String result = output.toString();
        output.setLength(0);
        return result;
    }

    public static <T> T fromString(Type type, String json)
    {
        return deserializer.deserialize(type, json);
    }
}
