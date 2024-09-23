package com.jfirer.dson.writer.impl.basic;

import com.jfirer.dson.writer.TypeWriter;

import java.util.Date;

public class DateWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        long time = ((Date) entity).getTime();
        output.append(time);
    }
}
