package com.jfirer.dson.reader.support.entry;

import com.jfirer.baseutil.reflect.ReflectUtil;
import com.jfirer.baseutil.reflect.valueaccessor.ValueAccessor;
import com.jfirer.dson.DsonConfig;
import com.jfirer.dson.DsonContext;
import com.jfirer.dson.reader.DeSerializeDefinition;
import com.jfirer.dson.reader.Stream;
import com.jfirer.dson.reader.TypeReader;

import java.lang.reflect.Field;

public class StandardReadEntry implements ReadEntry
{
    protected       String        name;
    protected       String        fieldName;
    protected final TypeReader    typeReader;
    protected final int           classId;
    protected final ValueAccessor valueAccessor;

    public StandardReadEntry(String name, Field field, DsonContext dsonContext)
    {
        this.name      = name;
        this.fieldName = field.getName();
        DsonConfig config = dsonContext.getConfig();
        valueAccessor = config.isValueAccessorUseCompile() ? ValueAccessor.compile(field) : ValueAccessor.standard(field);
        classId       = ReflectUtil.getClassId(field.getType());
        if (field.isAnnotationPresent(DeSerializeDefinition.class))
        {
            DeSerializeDefinition annotation = field.getAnnotation(DeSerializeDefinition.class);
            try
            {
                typeReader = annotation.value().newInstance();
                typeReader.initialize(field.getType(), dsonContext);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        else if (ReflectUtil.isNonBoxedObject(classId) && classId != ReflectUtil.CLASS_STRING)
        {
            typeReader = dsonContext.parseReader(field.getGenericType());
        }
        else
        {
            typeReader = null;
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
            case ReflectUtil.CLASS_INT -> valueAccessor.setReference(instance, stream.getWInt());
            case ReflectUtil.CLASS_LONG -> valueAccessor.setReference(instance, stream.getWLong());
            case ReflectUtil.CLASS_FLOAT -> valueAccessor.setReference(instance, stream.getWFloat());
            case ReflectUtil.CLASS_DOUBLE -> valueAccessor.setReference(instance, stream.getWDouble());
            case ReflectUtil.CLASS_BOOL -> valueAccessor.setReference(instance, stream.getBoolean());
            case ReflectUtil.CLASS_BYTE -> valueAccessor.setReference(instance, stream.getWByte());
            case ReflectUtil.CLASS_SHORT -> valueAccessor.setReference(instance, stream.getWShort());
            case ReflectUtil.CLASS_CHAR -> valueAccessor.setReference(instance, stream.getChar());
            case ReflectUtil.CLASS_STRING -> valueAccessor.setReference(instance, stream.getStringValue());
            default -> valueAccessor.setReference(instance, typeReader.fromString(stream));
        }
    }

    @Override
    public String name()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}



