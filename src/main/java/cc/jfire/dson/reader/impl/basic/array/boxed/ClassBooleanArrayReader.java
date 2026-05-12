package cc.jfire.dson.reader.impl.basic.array.boxed;

import cc.jfire.dson.reader.Stream;
import cc.jfire.dson.reader.TypeReader;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Map;

public class ClassBooleanArrayReader implements TypeReader
{
    @Override
    public Object fromString(Stream stream, Map<TypeVariable<?>, Type> typeVariableContext)
    {
        stream.startParseArray();
        int       count = 0;
        Boolean[] array = new Boolean[16];
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
                array[count] = stream.getBoolean();
                count += 1;
            }
            stream.skipComma();
        }
        return Arrays.copyOf(array, count);
    }
}
