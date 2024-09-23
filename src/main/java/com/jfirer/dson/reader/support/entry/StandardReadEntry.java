package com.jfirer.dson.reader.support.entry;

import com.jfirer.baseutil.reflect.ReflectUtil;
import com.jfirer.baseutil.reflect.valueaccessor.ValueAccessor;
import com.jfirer.dson.DsonConfig;
import com.jfirer.dson.reader.JsonReader;
import com.jfirer.dson.reader.Stream;
import com.jfirer.dson.reader.TypeReader;
import com.jfirer.dson.reader.DeSerializeDefinition;

import java.lang.reflect.Field;

public class StandardReadEntry implements ReadEntry
{
    protected       String        name;
    protected       String        fieldName;
    protected       TypeReader    typeReader;
    protected final int           classId;
    protected final ValueAccessor valueAccessor;

    public StandardReadEntry(String name, Field field, JsonReader jsonReader)
    {
        this.name      = name;
        this.fieldName = field.getName();
        DsonConfig config = jsonReader.getConfig();
        valueAccessor = config.isStandardReadUseCompile() ? ValueAccessor.compile(field) : ValueAccessor.standard(field);
        classId       = ReflectUtil.getClassId(field.getType());
        if (field.isAnnotationPresent(DeSerializeDefinition.class))
        {
            DeSerializeDefinition annotation = field.getAnnotation(DeSerializeDefinition.class);
            try
            {
                typeReader = annotation.value().newInstance();
                typeReader.init(field.getType(), jsonReader);
            }
            catch (Exception e)
            {
                ReflectUtil.throwException(e);
            }
        }
        else if (ReflectUtil.isNonBoxedObject(classId) && classId != ReflectUtil.CLASS_STRING)
        {
            typeReader = jsonReader.parse(field.getGenericType());
        }
    }

    @Override
    public void setValue(Object instance, Stream stream)
    {
        switch (classId)
        {
            case ReflectUtil.PRIMITIVE_INT -> valueAccessor.set(instance, stream.getInt());
            case ReflectUtil.PRIMITIVE_LONG -> valueAccessor.set(instance, stream.getLong());
            case ReflectUtil.PRIMITIVE_FLOAT -> valueAccessor.set(instance, stream.getFloat());
            case ReflectUtil.PRIMITIVE_DOUBLE -> valueAccessor.set(instance, stream.getDouble());
            case ReflectUtil.PRIMITIVE_BOOL -> valueAccessor.set(instance, stream.getBoolean());
            case ReflectUtil.PRIMITIVE_BYTE -> valueAccessor.set(instance, stream.getByte());
            case ReflectUtil.PRIMITIVE_SHORT -> valueAccessor.set(instance, stream.getShort());
            case ReflectUtil.PRIMITIVE_CHAR -> valueAccessor.set(instance, stream.getChar());
            case ReflectUtil.CLASS_INT -> valueAccessor.setReference(instance, stream.getInt());
            case ReflectUtil.CLASS_LONG -> valueAccessor.setReference(instance, stream.getLong());
            case ReflectUtil.CLASS_FLOAT -> valueAccessor.setReference(instance, stream.getFloat());
            case ReflectUtil.CLASS_DOUBLE -> valueAccessor.setReference(instance, stream.getDouble());
            case ReflectUtil.CLASS_BOOL -> valueAccessor.setReference(instance, stream.getBoolean());
            case ReflectUtil.CLASS_BYTE -> valueAccessor.setReference(instance, stream.getByte());
            case ReflectUtil.CLASS_SHORT -> valueAccessor.setReference(instance, stream.getShort());
            case ReflectUtil.CLASS_CHAR -> valueAccessor.setReference(instance, stream.getChar());
            case ReflectUtil.CLASS_STRING -> valueAccessor.setReference(instance, stream.getStringValue());
            default -> valueAccessor.setReference(instance, typeReader.fromString(stream));
        }
    }

    @Override
    public String toString()
    {
        return name;
    }
}



