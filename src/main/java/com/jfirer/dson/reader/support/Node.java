package com.jfirer.dson.reader.support;

import com.jfirer.dson.DsonConfig;
import com.jfirer.dson.reader.JsonReader;
import com.jfirer.dson.reader.support.entry.ReadEntry;
import com.jfirer.dson.util.JsonRenameStrategy;
import com.jfirer.dson.util.JsonIgnore;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class Node
{
    char      c;
    Node[]    next = new Node[90];
    ReadEntry readEntry;

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

    public ReadEntry getEntry()
    {
        return readEntry;
    }

    public void put(String name, ReadEntry readEntry)
    {
        char c = name.charAt(0);
        if (c <= 'z')
        {
            int index = c - 33;
            if (next[index] == null)
            {
                next[index] = new Node();
            }
            next[index].put(name, 0, readEntry);
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }

    public void put(String name, int i, ReadEntry readEntry)
    {
        if (i < name.length())
        {
            c = name.charAt(i);
            if (i + 1 == name.length())
            {
                this.readEntry = readEntry;
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
                next[index].put(name, i + 1, readEntry);
            }
        }
    }

    public static Node generateRoot(Class ckass, JsonReader jsonReader)
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
                map.putIfAbsent(JsonRenameStrategy.helpGetRename(each), each);
            }
            ckass = ckass.getSuperclass();
        }
        Node       rootNode = new Node();
        DsonConfig config   = jsonReader.getConfig();
        for (Map.Entry<String, Field> each : map.entrySet())
        {
            rootNode.put(each.getKey(), config.isReadUseCompile() ? ReadEntry.compile(each.getValue(), jsonReader) : ReadEntry.standard(each.getKey(), each.getValue(), jsonReader));
        }
        return rootNode;
    }
}
