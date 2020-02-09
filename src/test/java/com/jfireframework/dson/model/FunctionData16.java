package com.jfireframework.dson.model;

import com.jfireframework.dson.serializer.JsonWriter;
import com.jfireframework.dson.serializer.TypeWriter;
import com.jfireframework.dson.strategy.SerializeDefinition;

import java.lang.reflect.Type;

public class FunctionData16
{
    @SerializeDefinition(NameSeri.class)
    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public static final class NameSeri implements TypeWriter
    {

        @Override
        public void initialize(JsonWriter serializer, Type type)
        {
            // TODO Auto-generated method stub
        }

        @Override
        public void toJson(Object entity, StringBuilder output)
        {
            output.append('"').append("123").append('"');
        }
    }
}
