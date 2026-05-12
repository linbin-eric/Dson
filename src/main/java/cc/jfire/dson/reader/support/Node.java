package cc.jfire.dson.reader.support;

import cc.jfire.dson.DsonConfig;
import cc.jfire.dson.DsonContext;
import cc.jfire.dson.reader.support.entry.ReadEntry;
import cc.jfire.dson.util.JsonIgnore;
import cc.jfire.dson.util.JsonRenameStrategy;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

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

    public static Node generateRoot(Class ckass, Type type, DsonContext dsonContext, Map<TypeVariable<?>, Type> typeVariableContext)
    {
        Map<String, Field> map = new HashMap<String, Field>();
        while (ckass != Object.class)
        {
            Field[] fields = ckass.getDeclaredFields();
            for (Field each : fields)
            {
                int modifiers = each.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers) || (each.isAnnotationPresent(JsonIgnore.class) && each.getAnnotation(JsonIgnore.class).ignoreRead()))
                {
                    continue;
                }
                map.putIfAbsent(JsonRenameStrategy.helpGetRename(each), each);
            }
            ckass = ckass.getSuperclass();
        }
        Node                                 rootNode                = new Node();
        DsonConfig                           config                  = dsonContext.getConfig();
        ConcurrentMap<TypeVariable<?>, Type> thisTypeVariableContext = TypeResolver.resolveTypeArguments(type);
        thisTypeVariableContext.putAll(typeVariableContext);
        for (Map.Entry<String, Field> each : map.entrySet())
        {
            rootNode.put(each.getKey(), config.isReadEntryUseCompile() ? ReadEntry.compile(each.getValue(), dsonContext, thisTypeVariableContext) : ReadEntry.standard(each.getKey(), each.getValue(), dsonContext, thisTypeVariableContext));
        }
        return rootNode;
    }
}
