package com.jfirer.dson.reader.impl.basic.array;

import com.jfirer.dson.reader.Stream;
import com.jfirer.dson.reader.TypeReader;

import java.util.Arrays;

public class IntArrayReader implements TypeReader
{
    @Override
    public Object fromString(Stream stream)
    {
        stream.startParseArray();
        int   count = 0;
        int[] array = new int[16];
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
                array[count] = stream.getInt();
                count += 1;
            }
            stream.skipComma();
        }
        return Arrays.copyOf(array, count);
    }
}
