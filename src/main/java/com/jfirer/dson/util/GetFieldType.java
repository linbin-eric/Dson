package com.jfirer.dson.util;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class GetFieldType
{
    public static Type get(Class aClass, String fieldName)
    {
        do
        {
            try
            {
                Field field = aClass.getDeclaredField(fieldName);
                return field.getGenericType();
            }
            catch (NoSuchFieldException e)
            {
                aClass = aClass.getSuperclass();
            }
        } while (aClass != Object.class);
        throw new IllegalArgumentException("属性不存在");
    }
}
