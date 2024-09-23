package com.jfirer.dson.writer.impl.basic;

import com.jfirer.dson.writer.JsonWriter;
import com.jfirer.dson.writer.TypeWriter;

import java.lang.reflect.Type;
import java.util.Date;

public class DateWriter implements TypeWriter
{

    @Override
    public void initialize(JsonWriter writer, Type type)
    {
    }

    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        long time = ((Date) entity).getTime();
        output.append(time);
    }
}
