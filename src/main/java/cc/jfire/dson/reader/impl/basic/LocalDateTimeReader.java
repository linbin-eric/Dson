package cc.jfire.dson.reader.impl.basic;

import cc.jfire.baseutil.DateUtil;
import cc.jfire.dson.reader.Stream;
import cc.jfire.dson.reader.TypeReader;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.time.LocalDateTime;
import java.util.Map;

public class LocalDateTimeReader implements TypeReader
{
    @Override
    public Object fromString(Stream stream, Map<TypeVariable<?>, Type> typeVariableContext)
    {
        return LocalDateTime.parse(stream.getStringValue(), DateUtil.FORMATTER);
    }
}
