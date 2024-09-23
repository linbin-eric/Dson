package com.jfirer.dson.reader.support.entry;

import com.jfirer.baseutil.STR;
import com.jfirer.baseutil.reflect.ReflectUtil;
import com.jfirer.baseutil.smc.SmcHelper;
import com.jfirer.baseutil.smc.compiler.CompileHelper;
import com.jfirer.baseutil.smc.model.ClassModel;
import com.jfirer.baseutil.smc.model.FieldModel;
import com.jfirer.baseutil.smc.model.MethodModel;
import com.jfirer.dson.reader.JsonReader;
import com.jfirer.dson.reader.Stream;
import com.jfirer.dson.reader.TypeReader;
import com.jfirer.dson.reader.DeSerializeDefinition;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

public interface ReadEntry
{
    void setValue(Object instance, Stream stream);

    static ReadEntry standard(String name, Field field, JsonReader jsonReader)
    {
        return new StandardReadEntry(name, field, jsonReader);
    }

    @SneakyThrows
    static ReadEntry compile(Field field, JsonReader jsonReader)
    {
        TypeReader typeReader = null;
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
        ClassModel classModel = new ClassModel(STR.format("ReadEntry_{}_{}", field.getName(), CompileHelper.COMPILE_COUNTER.getAndIncrement()));
        classModel.addField(new FieldModel("typeReader", TypeReader.class, classModel));
        classModel.addConstructor("this.typeReader=$0;", TypeReader.class);
        classModel.addInterface(ReadEntry.class);
        MethodModel methodModel = new MethodModel(ReadEntry.class.getDeclaredMethod("setValue", Object.class, Stream.class), classModel);
        methodModel.setParamterNames("instance", "stream");
        int           classId    = ReflectUtil.getClassId(field.getType());
        String        methodName = ReflectUtil.parseBeanSetMethodName(field);
        String        typeName   = SmcHelper.getReferenceName(field.getDeclaringClass(), classModel);
        StringBuilder builder    = new StringBuilder();
        switch (classId)
        {
            case ReflectUtil.PRIMITIVE_BYTE, ReflectUtil.CLASS_BYTE -> builder.append(STR.format("(({})instance).{}(stream.getByte());", typeName, methodName));
            case ReflectUtil.PRIMITIVE_INT, ReflectUtil.CLASS_INT -> builder.append(STR.format("(({})instance).{}(stream.getInt());", typeName, methodName));
            case ReflectUtil.PRIMITIVE_SHORT, ReflectUtil.CLASS_SHORT -> builder.append(STR.format("(({})instance).{}(stream.getShort());", typeName, methodName));
            case ReflectUtil.PRIMITIVE_LONG, ReflectUtil.CLASS_LONG -> builder.append(STR.format("(({})instance).{}(stream.getLong());", typeName, methodName));
            case ReflectUtil.PRIMITIVE_FLOAT, ReflectUtil.CLASS_FLOAT -> builder.append(STR.format("(({})instance).{}(stream.getFloat());", typeName, methodName));
            case ReflectUtil.PRIMITIVE_DOUBLE, ReflectUtil.CLASS_DOUBLE -> builder.append(STR.format("(({})instance).{}(stream.getDouble());", typeName, methodName));
            case ReflectUtil.PRIMITIVE_BOOL, ReflectUtil.CLASS_BOOL -> builder.append(STR.format("(({})instance).{}(stream.getBoolean());", typeName, methodName));
            case ReflectUtil.PRIMITIVE_CHAR, ReflectUtil.CLASS_CHAR -> builder.append(STR.format("(({})instance).{}(stream.getChar());", typeName, methodName));
            case ReflectUtil.CLASS_STRING -> builder.append(STR.format("(({})instance).{}(stream.getStringValue());", typeName, methodName));
            default -> builder.append(STR.format("(({})instance).{}(({})typeReader.fromString(stream));", typeName, methodName, SmcHelper.getReferenceName(field.getType(), classModel)));
        }
        methodModel.setBody(builder.toString());
        classModel.putMethodModel(methodModel);
        Class<ReadEntry> compile = (Class<ReadEntry>) CompileHelper.DEFAULT_COMPILE_HELPER.compile(classModel);
        return compile.getConstructor(TypeReader.class).newInstance(typeReader);
    }
}
