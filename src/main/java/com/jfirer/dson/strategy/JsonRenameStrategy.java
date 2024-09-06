package com.jfirer.dson.strategy;

import com.jfirer.dson.util.JsonRename;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public interface JsonRenameStrategy
{
    String name(Field field);

    static String helpGetRename(Field field, JsonRenameStrategy strategy)
    {
        if (field.isAnnotationPresent(JsonRename.class) && field.getAnnotation(JsonRename.class).value().equals("") == false)
        {
            JsonRename jsonRename = field.getAnnotation(JsonRename.class);
            return jsonRename.value();
        }
        else
        {
            if (strategy != null)
            {
                return strategy.name(field);
            }
            else
            {
                return field.getName();
            }
        }
    }

    static JsonRenameStrategy helpGetStrategy(Class ckass)
    {
        if (ckass.isAnnotationPresent(JsonRename.class))
        {
            JsonRename jsonRename = (JsonRename) ckass.getAnnotation(JsonRename.class);
            if (jsonRename.strategy().equals(JsonRenameStrategy.class) == false)
            {
                try
                {
                    return jsonRename.strategy().getDeclaredConstructor().newInstance();
                }
                catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
                {
                    throw new RuntimeException(e);
                }
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }
}
