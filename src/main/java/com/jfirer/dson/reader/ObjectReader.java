package com.jfirer.dson.reader;

import com.jfirer.baseutil.reflect.ReflectUtil;
import com.jfirer.baseutil.reflect.ValueAccessor;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ObjectReader implements TypeReader
{
    class Entry
    {
        String        name;
        Field         field;
        TypeReader    typeReader;
        boolean       primitive;
        ValueAccessor valueAccessor;

        public Entry(String name, Field field)
        {
            this.name = name;
            this.field = field;
            primitive = field.getType().isPrimitive();
            valueAccessor = new ValueAccessor(field);
        }
    }

    Map<String, Entry> entryMap = new HashMap<String, Entry>();
    private JsonReader jsonReader;
    private Class      ckass;

    @Override
    public void init(Type type, JsonReader jsonReader)
    {
        this.jsonReader = jsonReader;
        this.ckass = (Class) type;
        Class              ckass = (Class) type;
        Map<String, Field> map   = new HashMap<String, Field>();
        while (ckass != Object.class)
        {
            Field[] fields = ckass.getDeclaredFields();
            for (Field each : fields)
            {
                if (map.containsKey(each.getName()))
                {
                    continue;
                }
                map.put(each.getName(), each);
            }
            ckass = ckass.getSuperclass();
        }
        for (Map.Entry<String, Field> each : map.entrySet())
        {
            entryMap.put(each.getKey(), new Entry(each.getKey(), each.getValue()));
        }
    }

    @Override
    public Object fromString(Stream stream)
    {
        try
        {
            Object instance = ckass.newInstance();
            stream.startParseObject();
            while (stream.parseObjectEnd() == false)
            {
                String name  = stream.getName();
                Entry  entry = entryMap.get(name);
                stream.skipColon();
                if (entry == null)
                {
                    stream.skipWholeValue();
                }
                else
                {
                    if (entry.primitive)
                    {
                        Class<?> fieldType = entry.field.getType();
                        if (fieldType == int.class)
                        {
                            int anInt = stream.getInt();
                            entry.valueAccessor.set(instance, anInt);
                        }
                    }
                    else
                    {
                        TypeReader typeReader = entry.typeReader;
                        if (typeReader == null)
                        {
                            entry.typeReader = typeReader = jsonReader.get(entry.field.getGenericType());
                        }
                        Object property = typeReader.fromString(stream);
                        entry.valueAccessor.setObject(instance, property);
                    }
                }
                stream.skipComma();
            }
            return instance;
        }
        catch (Exception e)
        {
            ReflectUtil.throwException(e);
            return null;
        }
    }
}
