package com.jfirer.dson.reader.support;

import com.jfirer.baseutil.smc.compiler.CompileHelper;
import com.jfirer.dson.reader.JsonReader;
import com.jfirer.dson.util.JsonIgnore;
import com.jfirer.dson.util.JsonRename;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class Node
{
    char   c;
    Node[] next = new Node[90];
    Entry  entry;

    public Node getNext(char c)
    {
        if (c <= 'z')
        {
            int index = c - 33;
            return next[index];
        }
        else
        {
            throw new IllegalArgumentException(String.valueOf(c));
        }
    }

    public Entry getEntry()
    {
        return entry;
    }

    public void put(String name, Entry entry)
    {
        char c = name.charAt(0);
        if (c <= 'z')
        {
            int index = c - 33;
            if (next[index] == null)
            {
                next[index] = new Node();
            }
            next[index].put(name, 0, entry);
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }

    public void put(String name, int i, Entry entry)
    {
        if (i < name.length())
        {
            c = name.charAt(i);
            if (i + 1 == name.length())
            {
                this.entry = entry;
            }
            else
            {
                char c1    = name.charAt(i + 1);
                int  index = 0;
                if (c1 <= 'z')
                {
                    index = c1 - 33;
                }
                else
                {
                    throw new IllegalArgumentException();
                }
                if (next[index] == null)
                {
                    next[index] = new Node();
                }
                next[index].put(name, i + 1, entry);
            }
        }
    }

    public static Node generateLambdaRoot(Class ckass, JsonReader jsonReader)
    {
        Map<String, Field> map = new HashMap<String, Field>();
        while (ckass != Object.class)
        {
            Field[] fields = ckass.getDeclaredFields();
            for (Field each : fields)
            {
                int modifiers = each.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers) || each.isAnnotationPresent(JsonIgnore.class))
                {
                    continue;
                }
                map.putIfAbsent(each.isAnnotationPresent(JsonRename.class) ? each.getAnnotation(JsonRename.class).value() : each.getName(), each);
            }
            ckass = ckass.getSuperclass();
        }
        Node rootNode = new Node();
        for (Map.Entry<String, Field> each : map.entrySet())
        {
            rootNode.put(each.getKey(), new Entry(each.getValue(), jsonReader));
        }
        return rootNode;
    }

    public static Node generateRoot(Class ckass, JsonReader jsonReader, CompileHelper compileHelper)
    {
        Map<String, Field> map = new HashMap<String, Field>();
        while (ckass != Object.class)
        {
            Field[] fields = ckass.getDeclaredFields();
            for (Field each : fields)
            {
                int modifiers = each.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers) || each.isAnnotationPresent(JsonIgnore.class))
                {
                    continue;
                }
                map.putIfAbsent(each.isAnnotationPresent(JsonRename.class) ? each.getAnnotation(JsonRename.class).value() : each.getName(), each);
            }
            ckass = ckass.getSuperclass();
        }
        Node rootNode = new Node();
        for (Map.Entry<String, Field> each : map.entrySet())
        {
            if (compileHelper == null)
            {
                rootNode.put(each.getKey(), new Entry(each.getKey(), each.getValue(), jsonReader));
            }
            else
            {
                try
                {
                    rootNode.put(each.getKey(), Entry.createSpecial(each.getValue(), jsonReader, compileHelper));
                }
                catch (IOException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        return rootNode;
    }
}
