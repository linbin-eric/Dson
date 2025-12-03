package cc.jfire.dson.writer.impl.basic;

import cc.jfire.dson.writer.TypeWriter;

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
