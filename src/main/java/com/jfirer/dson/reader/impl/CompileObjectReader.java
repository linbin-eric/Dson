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
import com.jfirer.dson.reader.support.Entry;
import com.jfirer.dson.reader.support.Node;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
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
        classModel.addImport(Stream.class);
        classModel.addImport(Entry.class);
        classModel.addField(new FieldModel("_rootNode_", Node.class, classModel));
        Node rootNode = Node.generateRoot((Class) type, jsonReader, compileHelper);
        classModel.addConstructor("this._rootNode_=$0;\r\n",  Node.class);
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
        body.append(SmcHelper.getReferenceName(Entry.class, classModel) + " entry = stream.getName(_rootNode_);\r\n");
        body.append("stream.skipColon();\r\n");
        body.append("if(entry==null){stream.skipWholeValue();}\r\n");
        body.append("else if(stream.isNextNullAndSkip()){;}\r\n");
        body.append("else{entry.setValue("+hostName+",stream);}\r\n");
        body.append("\r\n");
        body.append("stream.skipComma();");
        body.append("}\r\n");
        body.append("return " + hostName + ";\r\n");
        methodModel.setBody(body.toString());
        classModel.putMethodModel(methodModel);
        try
        {
            Class<?>       aClass      = compileHelper.compile(classModel);
            Constructor<?> constructor = aClass.getDeclaredConstructor( Node.class);
            Object         o           = constructor.newInstance(rootNode);
            function = (Function<Stream, Object>) o;
        }
        catch (IOException | ClassNotFoundException | NoSuchMethodException | InstantiationException |
               IllegalAccessException | InvocationTargetException e)
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
