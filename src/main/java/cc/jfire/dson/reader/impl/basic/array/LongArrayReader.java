package cc.jfire.dson.reader.impl.basic.array;

import cc.jfire.dson.reader.Stream;
import cc.jfire.dson.reader.TypeReader;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Map;

public class LongArrayReader implements TypeReader
{
    @Override
    public Object fromString(Stream stream, Map<TypeVariable<?>, Type> typeVariableContext)
    {
        stream.startParseArray();
        int    count = 0;
        long[] array = new long[16];
        while (stream.parseArrayEnd() == false)
        {
            if (count == array.length)
            {
                array = Arrays.copyOf(array, array.length * 2);
            }
            if (stream.isNextNullAndSkip())
            {
                ;
            }
            else
            {
                array[count] = stream.getLong();
                count += 1;
            }
            stream.skipComma();
        }
        return Arrays.copyOf(array, count);
    }
}
