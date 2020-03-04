package com.jfirer.dson.reader.impl;

import com.jfirer.baseutil.reflect.ReflectUtil;
import com.jfirer.baseutil.reflect.ValueAccessor;
import com.jfirer.baseutil.smc.compiler.CompileHelper;
import com.jfirer.dson.reader.JsonReader;
import com.jfirer.dson.reader.Stream;
import com.jfirer.dson.reader.TypeReader;

import java.beans.beancontext.BeanContext;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
        PrimitiveType primitiveType;
        ValueAccessor valueAccessor;

        public Entry(String name, Field field)
        {
            this.name = name;
            this.field = field;
            valueAccessor = new ValueAccessor(field);
            Class fieldType = field.getType();
            if (fieldType == int.class)
            {
                primitiveType = PrimitiveType.INT;
            }
            else if (fieldType == char.class)
            {
                primitiveType = PrimitiveType.CHAR;
            }
            else if (fieldType == long.class)
            {
                primitiveType = PrimitiveType.LONG;
            }
            else if (fieldType == short.class)
            {
                primitiveType = PrimitiveType.SHORT;
            }
            else if (fieldType == byte.class)
            {
                primitiveType = PrimitiveType.BYTE;
            }
            else if (fieldType == boolean.class)
            {
                primitiveType = PrimitiveType.BOOL;
            }
            else if (fieldType == float.class)
            {
                primitiveType = PrimitiveType.FLOAT;
            }
            else if (fieldType == double.class)
            {
                primitiveType = PrimitiveType.DOUBLE;
            }
            else
            {
                if (fieldType == Integer.class)
                {
                    primitiveType = PrimitiveType.W_INT;
                }
                else if (fieldType == Short.class)
                {
                    primitiveType = PrimitiveType.W_SHORT;
                }
                else if (fieldType == Byte.class)
                {
                    primitiveType = PrimitiveType.W_BYTE;
                }
                else if (fieldType == Long.class)
                {
                    primitiveType = PrimitiveType.W_LONG;
                }
                else if (fieldType == Character.class)
                {
                    primitiveType = PrimitiveType.W_CHAR;
                }
                else if (fieldType == Boolean.class)
                {
                    primitiveType = PrimitiveType.W_BOOL;
                }
                else if (fieldType == Float.class)
                {
                    primitiveType = PrimitiveType.W_FLOAT;
                }
                else if (fieldType == Double.class)
                {
                    primitiveType = PrimitiveType.W_DOUBLE;
                }
                else if (fieldType == String.class)
                {
                    primitiveType = PrimitiveType.STRING;
                }
                else
                {
                    primitiveType = PrimitiveType.NO;
                }
            }
        }
    }

    enum PrimitiveType
    {
        INT, BOOL, CHAR, BYTE, SHORT, LONG, FLOAT, DOUBLE, W_INT, W_BOOL, W_CHAR, W_BYTE, W_SHORT, W_LONG, W_FLOAT, W_DOUBLE, STRING, NO
    }

    private Map<String, Entry> entryMap = new HashMap<String, Entry>();
    private JsonReader         jsonReader;
    private Class              ckass;
    private ValueAccessor      constructor;

    @Override
    public void init(Type type, JsonReader jsonReader)
    {
        this.jsonReader = jsonReader;
        this.ckass = (Class) type;
        constructor = ValueAccessor.constructor(ckass,new CompileHelper());
        Class              ckass = (Class) type;
        Map<String, Field> map   = new HashMap<String, Field>();
        while (ckass != Object.class)
        {
            Field[] fields = ckass.getDeclaredFields();
            for (Field each : fields)
            {
                int modifiers = each.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers))
                {
                    continue;
                }
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
            Object instance = constructor.newInstace();
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
                    ValueAccessor valueAccessor = entry.valueAccessor;
                    switch (entry.primitiveType)
                    {
                        case INT:
                            valueAccessor.set(instance, stream.getInt());
                            break;
                        case BOOL:
                            valueAccessor.set(instance, stream.getBoolean());
                            break;
                        case CHAR:
                            valueAccessor.set(instance, stream.getChar());
                            break;
                        case BYTE:
                            valueAccessor.set(instance, stream.getByte());
                            break;
                        case SHORT:
                            valueAccessor.set(instance, stream.getShort());
                            break;
                        case LONG:
                            valueAccessor.set(instance, stream.getLong());
                            break;
                        case FLOAT:
                            valueAccessor.set(instance, stream.getFloat());
                            break;
                        case DOUBLE:
                            valueAccessor.set(instance, stream.getDouble());
                            break;
                        case W_INT:
                            valueAccessor.set(instance, stream.getWInt());
                            break;
                        case W_BOOL:
                            valueAccessor.set(instance, Boolean.valueOf(stream.getBoolean()));
                            break;
                        case W_BYTE:
                            valueAccessor.set(instance, stream.getWByte());
                            break;
                        case W_CHAR:
                            valueAccessor.set(instance, Character.valueOf(stream.getChar()));
                            break;
                        case W_LONG:
                            valueAccessor.set(instance, stream.getWLong());
                            break;
                        case W_FLOAT:
                            valueAccessor.set(instance, stream.getWFloat());
                            break;
                        case W_SHORT:
                            valueAccessor.set(instance, stream.getWShort());
                            break;
                        case W_DOUBLE:
                            valueAccessor.set(instance, stream.getWDouble());
                            break;
                        case STRING:
                            valueAccessor.setObject(instance, stream.getStringValue());
                            break;
                        case NO:
                        {
                            TypeReader typeReader = entry.typeReader;
                            if (typeReader == null)
                            {
                                entry.typeReader = typeReader = jsonReader.get(entry.field.getGenericType());
                            }
                            valueAccessor.setObject(instance, typeReader.fromString(stream));
                            break;
                        }
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
