package com.jfirer.dson.reader.impl;

import com.jfirer.baseutil.reflect.ReflectUtil;
import com.jfirer.baseutil.smc.SmcHelper;
import com.jfirer.baseutil.smc.compiler.CompileHelper;
import com.jfirer.baseutil.smc.model.ClassModel;
import com.jfirer.baseutil.smc.model.FieldModel;
import com.jfirer.baseutil.smc.model.MethodModel;
import com.jfirer.dson.reader.JsonReader;
import com.jfirer.dson.reader.Stream;
import com.jfirer.dson.reader.TypeReader;
import com.jfirer.dson.strategy.DeSerializeDefinition;
import com.jfirer.dson.util.GetFieldType;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class CompileObjectReader implements TypeReader
{
    private static final AtomicInteger            nameCount     = new AtomicInteger(0);
    private              Function<Stream, Object> function;
    private              CompileHelper            compileHelper = new CompileHelper();

    @Override
    public void init(Type type, JsonReader jsonReader)
    {
        ClassModel classModel = new ClassModel("CompileObjectReaderFor" + ((Class) type).getSimpleName() + "_" + nameCount.getAndIncrement());
        classModel.addInterface(Function.class);
        classModel.addImport(GetFieldType.class);
        classModel.addImport(Stream.class);
        FieldModel jsonReaderField = new FieldModel("jsonReader", JsonReader.class, classModel);
        classModel.addField(jsonReaderField);
        classModel.addConstructor("this.jsonReader=$0;\r\n", JsonReader.class);
        Method apply = null;
        try
        {
            apply = Function.class.getDeclaredMethod("apply", Object.class);
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        MethodModel   methodModel = new MethodModel(apply, classModel);
        StringBuilder body        = new StringBuilder();
        body.append("Stream stream = (Stream)$0;\r\n");
        String hostName = "_instance_" + nameCount.getAndIncrement();
        body.append(SmcHelper.getReferenceName((Class<?>) type, classModel) + " " + hostName + " = new " + SmcHelper.getReferenceName((Class<?>) type, classModel) + "();\r\n");
        body.append("stream.startParseObject();\r\n");
        body.append("while (stream.parseObjectEnd() == false){\r\n");
        body.append("String name  = stream.getName();\r\n");
        body.append("stream.skipColon();\r\n");
        body.append("switch(name){\r\n");
        while (type != Object.class)
        {
            for (Field each : ((Class<?>) type).getDeclaredFields())
            {
                if (Modifier.isFinal(each.getModifiers()) || Modifier.isStatic(each.getModifiers()))
                {
                    continue;
                }
                try
                {
                    Method method = ((Class<?>) type).getDeclaredMethod("set" + each.getName().substring(0, 1).toUpperCase() + each.getName().substring(1), each.getType());
                    body.append("case \"").append(each.getName()).append("\":{");
                    Class<?> fieldType = each.getType();
                    if (fieldType == int.class)
                    {
                        body.append(hostName).append('.').append(method.getName()).append("(stream.getInt());break;}\r\n");
                    }
                    else if (fieldType == short.class)
                    {
                        body.append(hostName).append('.').append(method.getName()).append("(stream.getShort());break;}\r\n");
                    }
                    else if (fieldType == long.class)
                    {
                        body.append(hostName).append('.').append(method.getName()).append("(stream.getLong());break;}\r\n");
                    }
                    else if (fieldType == boolean.class)
                    {
                        body.append(hostName).append('.').append(method.getName()).append("(stream.getBoolean());break;}\r\n");
                    }
                    else if (fieldType == byte.class)
                    {
                        body.append(hostName).append('.').append(method.getName()).append("(stream.getByte());break;}\r\n");
                    }
                    else if (fieldType == char.class)
                    {
                        body.append(hostName).append('.').append(method.getName()).append("(stream.getChar());break;}\r\n");
                    }
                    else if (fieldType == float.class)
                    {
                        body.append(hostName).append('.').append(method.getName()).append("(stream.getFloat());break;}\r\n");
                    }
                    else if (fieldType == double.class)
                    {
                        body.append(hostName).append('.').append(method.getName()).append("(stream.getDouble());break;}\r\n");
                    }
                    else if (fieldType == Integer.class)
                    {
                        body.append(hostName).append('.').append(method.getName()).append("(stream.getWInt());break;}\r\n");
                    }
                    else if (fieldType == Short.class)
                    {
                        body.append(hostName).append('.').append(method.getName()).append("(stream.getWShort());break;}\r\n");
                    }
                    else if (fieldType == Long.class)
                    {
                        body.append(hostName).append('.').append(method.getName()).append("(stream.getWLong());break;}\r\n");
                    }
                    else if (fieldType == Boolean.class)
                    {
                        body.append(hostName).append('.').append(method.getName()).append("(Boolean.valueOf(stream.getBoolean()));break;}\r\n");
                    }
                    else if (fieldType == Byte.class)
                    {
                        body.append(hostName).append('.').append(method.getName()).append("(stream.getWByte());break;}\r\n");
                    }
                    else if (fieldType == Character.class)
                    {
                        body.append(hostName).append('.').append(method.getName()).append("(Character.valueOf(stream.getChar()));break;}\r\n");
                    }
                    else if (fieldType == Float.class)
                    {
                        body.append(hostName).append('.').append(method.getName()).append("(stream.getWFloat());break;}\r\n");
                    }
                    else if (fieldType == Double.class)
                    {
                        body.append(hostName).append('.').append(method.getName()).append("(stream.getWDouble());break;}\r\n");
                    }
                    else if (fieldType == String.class)
                    {
                        body.append(hostName).append('.').append(method.getName()).append("(stream.getStringValue());break;}\r\n");
                    }
                    else
                    {
                        String     fieldName  = "typeReader_" + nameCount.getAndIncrement();
                        FieldModel fieldModel = new FieldModel(fieldName, TypeReader.class, classModel);
                        classModel.addField(fieldModel);
                        body.append(SmcHelper.getReferenceName(TypeReader.class, classModel) + " typeReader = ").append(fieldName).append(";\r\n");
                        if (each.isAnnotationPresent(DeSerializeDefinition.class))
                        {
                            Class<? extends TypeReader> value = each.getAnnotation(DeSerializeDefinition.class).value();
                            body.append("if(typeReader==null){\r\n").append("typeReader = new ").append(value.getName()).append("();\r\n");
                            body.append("typeReader.init(GetFieldType.get(" + SmcHelper.getReferenceName((Class<?>) type, classModel) + ".class,\"").append(each.getName()).append("\"),jsonReader);\r\n");
                            body.append(fieldName).append("=typeReader;\r\n}\r\n");
                        }
                        else
                        {
                            body.append("if(typeReader==null){").append(fieldName).append("=").append("typeReader=jsonReader.get(GetFieldType.get(" + SmcHelper.getReferenceName((Class<?>) type, classModel) + ".class,\"").append(each.getName()).append("\"));}");
                        }
                        body.append(hostName).append('.').append(method.getName()).append("((" + SmcHelper.getReferenceName(each.getType(), classModel) + ")typeReader.fromString(stream));break;}\r\n");
                    }
                    body.append("\r\n");
                }
                catch (NoSuchMethodException e)
                {
                    continue;
                }
            }
            type = ((Class) type).getSuperclass();
        }
        body.append("default: stream.skipWholeValue();");
        body.append("}\r\n");
        body.append("stream.skipComma();");
        body.append("}\r\n");
        body.append("return " + hostName + ";\r\n");
        methodModel.setBody(body.toString());
        classModel.putMethodModel(methodModel);
        try
        {
            Class<?>       aClass      = compileHelper.compile(classModel);
            Constructor<?> constructor = aClass.getDeclaredConstructor(JsonReader.class);
            Object         o           = constructor.newInstance(jsonReader);
            function = (Function<Stream, Object>) o;
        }
        catch (IOException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e)
        {
            ReflectUtil.throwException(e);
        }
    }

    @Override
    public Object fromString(Stream stream)
    {
        return function.apply(stream);
    }
}
