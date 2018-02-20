package com.jfireframework.dson.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.smc.SmcHelper;
import com.jfireframework.baseutil.smc.compiler.JavaStringCompiler;
import com.jfireframework.baseutil.smc.model.CompilerModel;
import com.jfireframework.baseutil.smc.model.MethodModel;
import com.jfireframework.dson.JsonProcessor;
import com.jfireframework.dson.serializer.PropertySerializer;
import com.jfireframework.dson.util.StringOutput;

public class CodePropertySerializerFactory implements PropertySerializerFactory
{
    private JsonProcessor jsonProcessor;
    
    @Override
    public void initialize(JsonProcessor jsonProcessor)
    {
        this.jsonProcessor = jsonProcessor;
    }
    
    @Override
    public PropertySerializer get(Class<?> type, String property)
    {
        Class<?> ckass = type;
        Field field = null;
        while (ckass != Object.class)
        {
            try
            {
                field = ckass.getDeclaredField(property);
                break;
            }
            catch (NoSuchFieldException e)
            {
                ckass = ckass.getSuperclass();
                continue;
            }
            catch (Throwable e)
            {
                throw new JustThrowException(e);
            }
        }
        if (field == null)
        {
            throw new NullPointerException();
        }
        Class<?> fieldType = field.getType();
        PropertySerializer propertySerializer = null;
        if (fieldType == int.class //
                || fieldType == short.class //
                || fieldType == long.class//
                || fieldType == float.class//
                || fieldType == double.class//
                || fieldType == byte.class//
                || fieldType == Byte.class//
                || Number.class.isAssignableFrom(fieldType))
        {
            propertySerializer = buildNumberPropertySerializer(type, property);
        }
        else if (fieldType == String.class || fieldType == Character.class || fieldType == char.class)
        {
            propertySerializer = buildStringPropertySerializer(type, property);
        }
        else if (fieldType == boolean.class || fieldType == Boolean.class)
        {
        }
        else if (Map.class.isAssignableFrom(fieldType))
        {
            
        }
        else if (Collection.class.isAssignableFrom(fieldType))
        {
            
        }
        else if (Iterator.class.isAssignableFrom(fieldType))
        {
            
        }
        else if (fieldType.isArray())
        {
            
        }
        else if (Modifier.isFinal(fieldType.getModifiers()))
        {
        }
        else
        {
        }
        propertySerializer.initialize(type, property);
        return propertySerializer;
    }
    
    PropertySerializer buildNumberPropertySerializer(Class<?> type, String propertyName)
    {
        CompilerModel compilerModel = new CompilerModel("NumberPropertySerializer", Object.class, PropertySerializer.class);
        try
        {
            Method initializeMethod = PropertySerializer.class.getDeclaredMethod("initialize", Class.class, String.class);
            MethodModel methodModel = new MethodModel(initializeMethod);
            methodModel.setBody("{}");
            compilerModel.putMethod(methodModel);
            Method serializeMethod = PropertySerializer.class.getDeclaredMethod("serialize", Object.class, StringOutput.class);
            methodModel = new MethodModel(serializeMethod);
            String body = "Number value = ((" + SmcHelper.getTypeName(type) + ")$0).get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1) + "();\r\n";
            body += "if(value==null){\r\n";
            body += "return false;\r\n";
            body += "}\r\n";
            body += "$1.append(\"\\\"" + propertyName + "\\\":\").append(value);\r\n";
            body += "return true;";
            methodModel.setBody(body);
            compilerModel.putMethod(methodModel);
            JavaStringCompiler compiler = new JavaStringCompiler();
            @SuppressWarnings("unchecked")
            Class<? extends PropertySerializer> compile = (Class<? extends PropertySerializer>) compiler.compile(compilerModel);
            PropertySerializer propertySerializer = compile.newInstance();
            return propertySerializer;
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
    }
    
    PropertySerializer buildStringPropertySerializer(Class<?> type, String propertyName)
    {
        CompilerModel compilerModel = new CompilerModel("StringPropertySerializer", Object.class, PropertySerializer.class);
        try
        {
            Method initializeMethod = PropertySerializer.class.getDeclaredMethod("initialize", Class.class, String.class);
            MethodModel methodModel = new MethodModel(initializeMethod);
            methodModel.setBody("{}");
            compilerModel.putMethod(methodModel);
            Method serializeMethod = PropertySerializer.class.getDeclaredMethod("serialize", Object.class, StringOutput.class);
            methodModel = new MethodModel(serializeMethod);
            String body = "String value = ((" + SmcHelper.getTypeName(type) + ")$0).get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1) + "();\r\n";
            body += "if(value==null){\r\n";
            body += "return false;\r\n";
            body += "}\r\n";
            body += "$1.append(\"\\\"" + propertyName + "\\\":\\\"\").append(value).append('\"');\r\n";
            body += "return true;";
            methodModel.setBody(body);
            compilerModel.putMethod(methodModel);
            JavaStringCompiler compiler = new JavaStringCompiler();
            @SuppressWarnings("unchecked")
            Class<? extends PropertySerializer> compile = (Class<? extends PropertySerializer>) compiler.compile(compilerModel);
            PropertySerializer propertySerializer = compile.newInstance();
            return propertySerializer;
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
    }
}
