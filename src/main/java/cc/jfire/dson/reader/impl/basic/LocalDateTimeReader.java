package cc.jfire.dson.reader.impl.basic;

import cc.jfire.baseutil.DateUtil;
import cc.jfire.dson.reader.Stream;
import cc.jfire.dson.reader.TypeReader;

import java.time.LocalDateTime;

public class LocalDateTimeReader implements TypeReader
{
    @Override
    public Object fromString(Stream stream)
    {
        return LocalDateTime.parse(stream.getStringValue(), DateUtil.FORMATTER);
    }
}
