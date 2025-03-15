package com.jfirer.dson.reader.impl.basic.array;

import com.jfirer.baseutil.reflect.ReflectUtil;
import com.jfirer.dson.reader.Stream;
import com.jfirer.dson.reader.TypeReader;

import java.util.Arrays;

public class FloatArrayReader implements TypeReader
{
    @Override
    public Object fromString(Stream stream)
    {
        stream.startParseArray();
        int     count = 0;
        float[] array = new float[16];
        try{

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
                array[count] = stream.getFloat();
                count += 1;
            }
            stream.skipComma();
        }
        }catch (Throwable e){
            ReflectUtil.throwException(e);
        }
        return Arrays.copyOf(array, count);
    }
}
