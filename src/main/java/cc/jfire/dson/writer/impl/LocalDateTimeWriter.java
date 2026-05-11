package cc.jfire.dson.writer.impl;

import cc.jfire.baseutil.DateUtil;
import cc.jfire.dson.writer.TypeWriter;

import java.time.LocalDateTime;

public class LocalDateTimeWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        output.append('"').append(DateUtil.FORMATTER.format(((LocalDateTime) entity))).append('"');
    }

    @Override
    public Object toJsonObject(Object entity)
    {
        return DateUtil.FORMATTER.format(((LocalDateTime) entity));
    }
}
