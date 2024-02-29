package com.jfirer.dson.writer.impl;

import com.jfirer.baseutil.reflect.CompileValueAccessor;
import com.jfirer.baseutil.reflect.ReflectUtil;
import com.jfirer.baseutil.reflect.ValueAccessor;
import com.jfirer.baseutil.smc.compiler.CompileHelper;
import com.jfirer.dson.strategy.SerializeDefinition;
import com.jfirer.dson.util.JsonIgnore;
import com.jfirer.dson.util.WriterUtil;
import com.jfirer.dson.writer.JsonWriter;
import com.jfirer.dson.writer.TypeWriter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

public class ObjectWriter implements TypeWriter
{
    private Entry[]    entries;
    private JsonWriter jsonWriter;
    private boolean    useCompile;

    public ObjectWriter(boolean useCompile)
    {
        this.useCompile = useCompile;
    }

    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        if (entries.length == 0)
        {
            return;
        }
        output.append('{');
        int length = output.length();
        for (Entry each : entries)
        {
            switch (each.type)
            {
                case STRING ->
                {
                    Object str = each.valueAccessor.get(entity);
                    if (str != null)
                    {
                        output.append(each.fullName);
                        output.append('"');
                        WriterUtil.writeString(output, (String) str);
                        output.append("\",");
                    }
                }
                case CHAR ->
                {
                    output.append(each.fullName);
                    output.append('"').append(each.valueAccessor.getChar(entity));
                    output.append('"').append(',');
                }
                case BOOL ->
                {
                    output.append(each.fullName);
                    output.append(each.valueAccessor.getBoolean(entity));
                    output.append(',');
                }
                case INT ->
                {
                    output.append(each.fullName);
                    output.append(each.valueAccessor.getInt(entity));
                    output.append(',');
                }
                case BYTE ->
                {
                    output.append(each.fullName);
                    output.append(each.valueAccessor.getByte(entity));
                    output.append(',');
                }
                case LONG ->
                {
                    output.append(each.fullName);
                    output.append(each.valueAccessor.getLong(entity));
                    output.append(',');
                }
                case FLOAT ->
                {
                    output.append(each.fullName);
                    output.append(each.valueAccessor.getFloat(entity));
                    output.append(',');
                }
                case SHORT ->
                {
                    output.append(each.fullName);
                    output.append(each.valueAccessor.getShort(entity));
                    output.append(',');
                }
                case DOUBLE ->
                {
                    output.append(each.fullName);
                    output.append(each.valueAccessor.getDouble(entity));
                    output.append(',');
                }
                case CUSTOM ->
                {
                    Object o = each.valueAccessor.get(entity);
                    if (o != null)
                    {
                        output.append(each.fullName);
                        each.typeWriter.toJson(o, output);
                        output.append(',');
                    }
                }
                case FINAL_OBJECT ->
                {
                    Object o = each.valueAccessor.get(entity);
                    if (o != null)
                    {
                        TypeWriter typeWriter = each.typeWriter;
                        if (typeWriter == null)
                        {
                            each.typeWriter = typeWriter = jsonWriter.get(each.field.getGenericType());
                        }
                        output.append(each.fullName);
                        typeWriter.toJson(o, output);
                        output.append(',');
                    }
                }
                case NOT_FINAL_OBJECT ->
                {
                    Object o = each.valueAccessor.get(entity);
                    if (o != null)
                    {
                        output.append(each.fullName);
                        jsonWriter.toJson(o, output);
                        output.append(',');
                    }
                }
            }
        }
        int newLength = output.length();
        if (length != newLength)
        {
            output.setLength(newLength - 1);
        }
        output.append('}');
    }

    public static List<Field> getAllSortedFields(Class type)
    {
        List<Field> fields = new LinkedList<Field>();
        List<Field> tmp    = new ArrayList<Field>();
        while (type != Object.class && type.isPrimitive() == false)
        {
            for (Field each : type.getDeclaredFields())
            {
                if (!each.isAnnotationPresent(JsonIgnore.class))
                {
                    tmp.add(each);
                }
            }
            Collections.sort(tmp, new Comparator<Field>()
            {
                @Override
                public int compare(Field o1, Field o2)
                {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            fields.addAll(tmp);
            tmp.clear();
            type = type.getSuperclass();
        }
        return fields;
    }

    class Entry
    {
        ValueAccessor valueAccessor;
        Field         field;
        PropertyType  type;
        TypeWriter    typeWriter;
        String        name;
        String        fullName;
    }

    enum PropertyType
    {
        STRING,//
        INT, BYTE, SHORT, LONG, FLOAT, DOUBLE, BOOL, CHAR,//
        CUSTOM, NOT_FINAL_OBJECT, FINAL_OBJECT
    }

    private CompileHelper compileHelper = new CompileHelper();

    @Override
    public void initialize(JsonWriter jsonWriter, Type type)
    {
        this.jsonWriter = jsonWriter;
        List<Entry> entries = new ArrayList<Entry>();
        for (Field field : getAllSortedFields((Class<?>) type))
        {
            if (field.getName().contains("this") || Modifier.isStatic(field.getModifiers()))
            {
                continue;
            }
            Class<?> fieldType = field.getType();
            Entry    entry     = new Entry();
            if (useCompile)
            {
                entry.valueAccessor = CompileValueAccessor.create(field, new CompileHelper());
            }
            else
            {
                entry.valueAccessor = new ValueAccessor(field);
            }
            entry.field    = field;
            entry.name     = field.getName();
            entry.fullName = '"' + entry.name + '"' + ':';
            entries.add(entry);
            if (fieldType.isPrimitive())
            {
                if (fieldType == int.class)
                {
                    entry.type = PropertyType.INT;
                }
                else if (fieldType == byte.class)
                {
                    entry.type = PropertyType.BYTE;
                }
                else if (fieldType == short.class)
                {
                    entry.type = PropertyType.SHORT;
                }
                else if (fieldType == long.class)
                {
                    entry.type = PropertyType.LONG;
                }
                else if (fieldType == float.class)
                {
                    entry.type = PropertyType.FLOAT;
                }
                else if (fieldType == double.class)
                {
                    entry.type = PropertyType.DOUBLE;
                }
                else if (fieldType == byte.class)
                {
                    entry.type = PropertyType.BYTE;
                }
                else if (fieldType == char.class)
                {
                    entry.type = PropertyType.CHAR;
                }
                else if (fieldType == boolean.class)
                {
                    entry.type = PropertyType.BOOL;
                }
            }
            else if (fieldType == String.class)
            {
                entry.type = PropertyType.STRING;
            }
            else if (Modifier.isFinal(fieldType.getModifiers()))
            {
                entry.type = PropertyType.FINAL_OBJECT;
            }
            else
            {
                entry.type = PropertyType.NOT_FINAL_OBJECT;
            }
            if (field.isAnnotationPresent(SerializeDefinition.class))
            {
                try
                {
                    entry.type       = PropertyType.CUSTOM;
                    entry.typeWriter = field.getAnnotation(SerializeDefinition.class).value().newInstance();
                    entry.typeWriter.initialize(jsonWriter, field.getGenericType());
                }
                catch (Exception e)
                {
                    ReflectUtil.throwException(e);
                }
            }
        }
        this.entries = entries.toArray(new Entry[0]);
    }
}
