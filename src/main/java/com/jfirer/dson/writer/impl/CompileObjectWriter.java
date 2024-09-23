package com.jfirer.dson.writer.impl;

import com.jfirer.baseutil.reflect.ReflectUtil;
import com.jfirer.baseutil.smc.SmcHelper;
import com.jfirer.baseutil.smc.compiler.CompileHelper;
import com.jfirer.baseutil.smc.model.ClassModel;
import com.jfirer.baseutil.smc.model.FieldModel;
import com.jfirer.baseutil.smc.model.MethodModel;
import com.jfirer.dson.writer.JsonWriter;
import com.jfirer.dson.writer.TypeWriter;
import com.jfirer.dson.writer.SerializeDefinition;
import com.jfirer.dson.util.WriterUtil;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CompileObjectWriter implements TypeWriter
{
    private static final AtomicInteger count = new AtomicInteger(0);
    private              CompileOutput compileOutput;

    @Override
    public void initialize(JsonWriter writer, Type type)
    {
        ClassModel classModel = new ClassModel("compile_" + count.getAndIncrement());
        classModel.addInterface(CompileOutput.class);
        MethodModel methodModel = null;
        try
        {
            Method toJson = CompileOutput.class.getDeclaredMethod("toJson", StringBuilder.class, Object.class);
            methodModel = new MethodModel(toJson, classModel);
            methodModel.setParamterNames("output", "entity");
        }
        catch (NoSuchMethodException e)
        {
            ReflectUtil.throwException(e);
        }
        StringBuilder methodBody = new StringBuilder();
        methodBody.append(SmcHelper.getReferenceName((Class<?>) type, classModel)).append(" _target = (").append(SmcHelper.getReferenceName((Class<?>) type, classModel)).append(")entity;\r\n");
        int count = 0;
        methodBody.append("output.append('{');\r\n");
        methodBody.append("int _len = output.length();\r\n");
        List<TypeWriter> customWriters = new ArrayList<TypeWriter>();
        List<String>     customNames   = new ArrayList<String>();
        for (Field each : ObjectWriter.getAllSortedFields((Class) type))
        {
            if (isMethodExist(each, (Class) type) == false)
            {
                continue;
            }
            if (each.isAnnotationPresent(SerializeDefinition.class))
            {
                Class<? extends TypeWriter> value      = each.getAnnotation(SerializeDefinition.class).value();
                TypeWriter                  typeWriter = null;
                try
                {
                    typeWriter = value.newInstance();
                    typeWriter.initialize(writer, each.getType());
                    String     fieldname  = "_custom_" + count++;
                    FieldModel fieldModel = new FieldModel(fieldname, TypeWriter.class, classModel);
                    classModel.addField(fieldModel);
                    customNames.add(fieldname);
                    customWriters.add(typeWriter);
                    String argName = "_arg_" + count++;
                    methodBody.append(SmcHelper.getReferenceName(each.getType(), classModel)).append(" ").append(argName).append(" = _target.").append(getMethodName(each)).append("();\r\n");
                    if (each.getType().isPrimitive())
                    {
                        methodBody.append("output.append(\"\\\"" + each.getName() + "\\\":\");\r\n");
                        methodBody.append(fieldname).append(".toJson(" + argName + ",output);\r\n");
                        methodBody.append("output.append(',');\r\n");
                    }
                    else
                    {
                        methodBody.append("if(").append(argName).append(" != null){\r\n");
                        methodBody.append("output.append(\"\\\"" + each.getName() + "\\\":\");\r\n");
                        methodBody.append(fieldname).append(".toJson(" + argName + ",output);\r\n");
                        methodBody.append("output.append(',');\r\n");
                        methodBody.append("}\r\n");
                    }
                }
                catch (Exception e)
                {
                    ReflectUtil.throwException(e);
                }
            }
            else if (each.getType().isPrimitive())
            {
                methodBody.append("output.append(\"\\\"" + each.getName() + "\\\":\")");
                if (each.getType() == char.class)
                {
                    methodBody.append(".append('\"').append(").append("_target.").append(getMethodName(each)).append("()).append(\"\\\",\");\r\n");
                }
                else
                {
                    methodBody.append(".append(_target.").append(getMethodName(each)).append("()).append(',');\r\n");
                }
            }
            else if (each.getType() == String.class)
            {
                String argName = "_arg_" + count++;
                methodBody.append("String ").append(argName).append(" = _target.").append(getMethodName(each)).append("();\r\n");
                methodBody.append("if(").append(argName).append(" != null){\r\n");
                methodBody.append("output.append(\"\\\"" + each.getName() + "\\\":\\\"\");");
                methodBody.append(WriterUtil.class.getName()).append(".writeString(output,").append(argName).append(");\r\n");
                methodBody.append("output.append(\"\\\",\");\r\n");
                methodBody.append("}\r\n");
            }
            else if (Modifier.isFinal(each.getType().getModifiers()))
            {
                String     typeWriterName = "finalWriter_" + count++;
                FieldModel fieldModel     = new FieldModel(typeWriterName, TypeWriter.class, classModel);
                classModel.addField(fieldModel);
                String argName = "_arg_" + count++;
                methodBody.append(SmcHelper.getReferenceName(each.getType(), classModel)).append(" ").append(argName).append(" = _target.").append(getMethodName(each)).append("();\r\n");
                methodBody.append("if(").append(argName).append(" != null){\r\n");
                methodBody.append("output.append(\"\\\"" + each.getName() + "\\\":\");\r\n");
                String tmpName = "writer_" + count++;
                methodBody.append("TypeWriter ").append(tmpName).append(" = ").append(typeWriterName).append(";\r\n");
                methodBody.append("\tif(").append(tmpName).append("==null){\r\n");
                methodBody.append(typeWriterName).append("=").append(tmpName).append(" = jsonWriter.get(").append(SmcHelper.getReferenceName(each.getType(), classModel)).append(".class);\r\n");
                methodBody.append("\t}\r\n");
                methodBody.append(tmpName).append(".toJson(" + argName + ",output);\r\n");
                methodBody.append("output.append(',');\r\n");
                methodBody.append("}\r\n");
            }
            else
            {
                String argName = "_arg_" + count++;
                methodBody.append(SmcHelper.getReferenceName(each.getType(), classModel)).append(" ").append(argName).append(" = _target.").append(getMethodName(each)).append("();\r\n");
                methodBody.append("if(").append(argName).append(" != null){\r\n");
                methodBody.append("output.append(\"\\\"" + each.getName() + "\\\":\");\r\n");
                methodBody.append("jsonWriter.toJson(" + argName + ",output);\r\n");
                methodBody.append("output.append(',');\r\n");
                methodBody.append("}\r\n");
            }
        }
        methodBody.append("if(_len != output.length()){output.setLength(output.length()-1);}\r\n");
        methodBody.append("output.append('}');\r\n");
        methodModel.setBody(methodBody.toString());
        classModel.putMethodModel(methodModel);
        FieldModel fieldModel = new FieldModel("jsonWriter", JsonWriter.class, classModel);
        classModel.addField(fieldModel);
        Class[] paramTypes = new Class[1 + customNames.size()];
        paramTypes[0] = JsonWriter.class;
        for (int i = 0; i < customNames.size(); i++)
        {
            paramTypes[i + 1] = TypeWriter.class;
        }
        String constructorBody = "jsonWriter = $0;\r\n";
        for (int i = 0; i < customNames.size(); i++)
        {
            constructorBody += customNames.get(i) + "=$" + (i + 1) + ";\r\n";
        }
        classModel.addConstructor(constructorBody, paramTypes);
        try
        {
            Class<CompileOutput>       ckass       = (Class<CompileOutput>) new CompileHelper().compile(classModel);
            Constructor<CompileOutput> constructor = ckass.getConstructor(paramTypes);
            List<Object>               params      = new ArrayList<Object>();
            params.add(writer);
            params.addAll(customWriters);
            compileOutput = constructor.newInstance(params.toArray(new Object[0]));
        }
        catch (Exception e)
        {
            ReflectUtil.throwException(e);
        }
    }

    String getMethodName(Field field)
    {
        String methodName;
        if (field.getType() == boolean.class)
        {
            methodName = "is" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        }
        else
        {
            methodName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        }
        return methodName;
    }

    boolean isMethodExist(Field field, Class ckass)
    {
        String methodName = getMethodName(field);
        while (ckass != Object.class && ckass.isPrimitive() == false) try
        {
            ckass.getDeclaredMethod(methodName);
            return true;
        }
        catch (NoSuchMethodException e)
        {
            ckass = ckass.getSuperclass();
        }
        return false;
    }

    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        compileOutput.toJson(output, entity);
    }

    public interface CompileOutput
    {
        void toJson(StringBuilder output, Object entity);
    }
}
