package com.jfirer.dson.util;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface JsonRenameStrategy
{
    String name(Field field);

    Map<Class<? extends JsonRenameStrategy>, JsonRenameStrategy> STRATEGY_MAP = new ConcurrentHashMap<>();

    @SneakyThrows
    static String helpGetRename(Field field)
    {
        if (field.isAnnotationPresent(JsonRename.class) && field.getAnnotation(JsonRename.class).value().equals("") == false)
        {
            JsonRename jsonRename = field.getAnnotation(JsonRename.class);
            return jsonRename.value();
        }
        else if (field.getDeclaringClass().isAnnotationPresent(JsonRename.class))
        {
            Class<? extends JsonRenameStrategy> strategy = field.getDeclaringClass().getAnnotation(JsonRename.class).strategy();
            if (strategy != JsonRenameStrategy.class)
            {
                JsonRenameStrategy jsonRenameStrategy = STRATEGY_MAP.computeIfAbsent(strategy, ckazz -> {
                    try
                    {
                        return ckazz.getConstructor().newInstance();
                    }
                    catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
                    {
                        throw new RuntimeException(e);
                    }
                });
                return jsonRenameStrategy.name(field);
            }
            else
            {
                return field.getName();
            }
        }
        else
        {
            return field.getName();
        }
    }
}
