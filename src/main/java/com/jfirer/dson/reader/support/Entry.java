package com.jfirer.dson.reader.support;

import com.jfirer.baseutil.reflect.ReflectUtil;
import com.jfirer.baseutil.reflect.ValueAccessor;
import com.jfirer.baseutil.smc.SmcHelper;
import com.jfirer.baseutil.smc.compiler.CompileHelper;
import com.jfirer.baseutil.smc.model.ClassModel;
import com.jfirer.baseutil.smc.model.MethodModel;
import com.jfirer.dson.reader.JsonReader;
import com.jfirer.dson.reader.Stream;
import com.jfirer.dson.reader.TypeReader;
import com.jfirer.dson.strategy.DeSerializeDefinition;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;

public class Entry
{
    protected            String        name;
    public               TypeReader    typeReader;
    public               PrimitiveType primitiveType;
    public               ValueAccessor valueAccessor;
    private static final AtomicInteger nameCount = new AtomicInteger();

    public static Entry createSpecial(Field field, JsonReader jsonReader, CompileHelper compileHelper) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException
    {
        ClassModel  classModel  = new ClassModel("EnhanceEntryFor" + field.getName() + nameCount.getAndIncrement(), Entry.class);
        MethodModel methodModel = new MethodModel(classModel);
        methodModel.setMethodName("setValue");
        methodModel.setAccessLevel(MethodModel.AccessLevel.PUBLIC);
        methodModel.setReturnType(void.class);
        methodModel.setParamterTypes(Object.class, Stream.class);
        methodModel.setParamterNames("object", "stream");
        String     instanceName = "((" + SmcHelper.getReferenceName(field.getDeclaringClass(), classModel) + ")object).";
        Class      fieldType    = field.getType();
        String     fieldName    = field.getName();
        TypeReader typeReader   = null;
        if (fieldType == int.class || fieldType == Integer.class)
        {
            methodModel.setBody(instanceName + "set" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1) + "(stream.getInt());");
        }
        else if (fieldType == char.class || fieldType == Character.class)
        {
            methodModel.setBody(instanceName + "set" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1) + "(stream.getChar());");
        }
        else if (fieldType == long.class || fieldType == Long.class)
        {
            methodModel.setBody(instanceName + "set" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1) + "(stream.getLong());");
        }
        else if (fieldType == short.class || fieldType == Short.class)
        {
            methodModel.setBody(instanceName + "set" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1) + "(stream.getShort());");
        }
        else if (fieldType == byte.class || fieldType == Byte.class)
        {
            methodModel.setBody(instanceName + "set" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1) + "(stream.getByte());");
        }
        else if (fieldType == boolean.class || fieldType == Boolean.class)
        {
            methodModel.setBody(instanceName + "set" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1) + "(stream.getBoolean());");
        }
        else if (fieldType == float.class || fieldType == Float.class)
        {
            methodModel.setBody(instanceName + "set" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1) + "(stream.getFloat());");
        }
        else if (fieldType == double.class || fieldType == Double.class)
        {
            methodModel.setBody(instanceName + "set" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1) + "(stream.getDouble());");
        }
        else if (fieldType == String.class)
        {
            methodModel.setBody(instanceName + "set" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1) + "(stream.getStringValue());");
        }
        else
        {
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
            else
            {
                typeReader = jsonReader.get(field.getGenericType());
            }
            methodModel.setBody(instanceName + "set" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1) + "((" + SmcHelper.getReferenceName(fieldType, classModel) + ")typeReader.fromString(stream));");
        }
        classModel.putMethodModel(methodModel);
        if (typeReader != null)
        {
            classModel.addConstructor("super($0);", TypeReader.class);
            Class<? extends Entry> compile = (Class<? extends Entry>) compileHelper.compile(classModel);
            return compile.getConstructor(TypeReader.class).newInstance(typeReader);
        }
        else
        {
            classModel.addConstructor("");
            Class<? extends Entry> compile = (Class<? extends Entry>) compileHelper.compile(classModel);
            return compile.getConstructor().newInstance();
        }
    }

    public Entry(TypeReader typeReader)
    {
        this.typeReader = typeReader;
    }

    public Entry()
    {
    }

    public Entry(String name, Field field, JsonReader jsonReader)
    {
        this.name = name;
        valueAccessor = new ValueAccessor(field);
        Class fieldType = field.getType();
        if (fieldType == int.class || fieldType == Integer.class)
        {
            primitiveType = PrimitiveType.INT;
        }
        else if (fieldType == char.class || fieldType == Character.class)
        {
            primitiveType = PrimitiveType.CHAR;
        }
        else if (fieldType == long.class || fieldType == Long.class)
        {
            primitiveType = PrimitiveType.LONG;
        }
        else if (fieldType == short.class || fieldType == Short.class)
        {
            primitiveType = PrimitiveType.SHORT;
        }
        else if (fieldType == byte.class || fieldType == Byte.class)
        {
            primitiveType = PrimitiveType.BYTE;
        }
        else if (fieldType == boolean.class || fieldType == Boolean.class)
        {
            primitiveType = PrimitiveType.BOOL;
        }
        else if (fieldType == float.class || fieldType == Float.class)
        {
            primitiveType = PrimitiveType.FLOAT;
        }
        else if (fieldType == double.class || fieldType == Double.class)
        {
            primitiveType = PrimitiveType.DOUBLE;
        }
        else if (fieldType == String.class)
        {
            primitiveType = PrimitiveType.STRING;
        }
        else
        {
            primitiveType = PrimitiveType.NO;
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
            else
            {
                typeReader = jsonReader.get(field.getGenericType());
            }
        }
    }

    public void setValue(Object instance, Stream stream)
    {
        switch (primitiveType)
        {
            case INT -> valueAccessor.set(instance, stream.getInt());
            case BOOL -> valueAccessor.set(instance, stream.getBoolean());
            case CHAR -> valueAccessor.set(instance, stream.getChar());
            case BYTE -> valueAccessor.set(instance, stream.getByte());
            case SHORT -> valueAccessor.set(instance, stream.getShort());
            case LONG -> valueAccessor.set(instance, stream.getLong());
            case FLOAT -> valueAccessor.set(instance, stream.getFloat());
            case DOUBLE -> valueAccessor.set(instance, stream.getDouble());
            case STRING -> valueAccessor.setObject(instance, stream.getStringValue());
            case NO -> valueAccessor.setObject(instance, typeReader.fromString(stream));
        }
    }
}
