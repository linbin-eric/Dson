package cc.jfire.dson.reader.impl.basic.array.boxed;

import cc.jfire.dson.reader.Stream;
import cc.jfire.dson.reader.TypeReader;

import java.util.Arrays;

public class ClassCharArrayReader implements TypeReader
{
    @Override
    public Object fromString(Stream stream)
    {
        stream.startParseArray();
        int         count = 0;
        Character[] array = new Character[16];
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
                array[count] = stream.getChar();
                count += 1;
            }
            stream.skipComma();
        }
        return Arrays.copyOf(array, count);
    }
}
