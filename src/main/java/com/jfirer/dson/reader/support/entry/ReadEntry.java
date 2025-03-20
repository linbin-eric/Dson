package com.jfirer.dson.reader.support.entry;

import com.jfirer.baseutil.STR;
import com.jfirer.baseutil.reflect.ReflectUtil;
import com.jfirer.baseutil.smc.SmcHelper;
import com.jfirer.baseutil.smc.compiler.CompileHelper;
import com.jfirer.baseutil.smc.model.ClassModel;
import com.jfirer.baseutil.smc.model.FieldModel;
import com.jfirer.baseutil.smc.model.MethodModel;
import com.jfirer.dson.DsonContext;
import com.jfirer.dson.reader.DeSerializeDefinition;
import com.jfirer.dson.reader.Stream;
import com.jfirer.dson.reader.TypeReader;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

public interface ReadEntry
{
    void setValue(Object instance, Stream stream);

    /**
     * 这个entry的实际输入输出名称
     *
     * @return
     */
    String name();

    static ReadEntry standard(String name, Field field, DsonContext dsonContext)
    {
        return new StandardReadEntry(name, field, dsonContext);
    }

    @SneakyThrows
    static ReadEntry compile(Field field, DsonContext dsonContext)
    {
        TypeReader typeReader = null;
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
                ReflectUtil.throwException(e);
            }
        }
        else if (ReflectUtil.getClassId(field.getType()) > ReflectUtil.CLASS_STRING)
        {
            typeReader = dsonContext.parseReader(field.getGenericType());
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
        if (typeReader != null)
        {
            switch (classId)
            {
                case ReflectUtil.PRIMITIVE_BYTE, ReflectUtil.CLASS_BYTE -> builder.append(STR.format("(({})instance).{}((Byte)typeReader.from(stream));", typeName, methodName));
                case ReflectUtil.PRIMITIVE_INT, ReflectUtil.CLASS_INT -> builder.append(STR.format("(({})instance).{}((Integer)typeReader.from(stream));", typeName, methodName));
                case ReflectUtil.PRIMITIVE_SHORT, ReflectUtil.CLASS_SHORT -> builder.append(STR.format("(({})instance).{}((Short)typeReader.from(stream));", typeName, methodName));
                case ReflectUtil.PRIMITIVE_LONG, ReflectUtil.CLASS_LONG -> builder.append(STR.format("(({})instance).{}((Long)typeReader.from(stream));", typeName, methodName));
                case ReflectUtil.PRIMITIVE_FLOAT, ReflectUtil.CLASS_FLOAT -> builder.append(STR.format("(({})instance).{}((Float)typeReader.from(stream));", typeName, methodName));
                case ReflectUtil.PRIMITIVE_DOUBLE,
                     ReflectUtil.CLASS_DOUBLE -> builder.append(STR.format("(({})instance).{}((Double)typeReader.from(stream));", typeName, methodName));
                case ReflectUtil.PRIMITIVE_BOOL, ReflectUtil.CLASS_BOOL -> builder.append(STR.format("(({})instance).{}((Boolean)typeReader.from(stream));", typeName, methodName));
                case ReflectUtil.PRIMITIVE_CHAR,
                     ReflectUtil.CLASS_CHAR -> builder.append(STR.format("(({})instance).{}((Character)typeReader.from(stream));", typeName, methodName));
                default -> builder.append(STR.format("(({})instance).{}(({})typeReader.fromString(stream));", typeName, methodName, SmcHelper.getReferenceName(field.getType(), classModel)));
            }
        }
        else
        {
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
                default -> throw new IllegalArgumentException();
            }
        }
        methodModel.setBody(builder.toString());
        classModel.putMethodModel(methodModel);
        MethodModel fieldNameModel = new MethodModel(ReadEntry.class.getDeclaredMethod("name"), classModel);
        fieldNameModel.setBody(STR.format("return \"{}\";", field.getName()));
        classModel.putMethodModel(fieldNameModel);
        Class<ReadEntry> compile = (Class<ReadEntry>) CompileHelper.DEFAULT_COMPILE_HELPER.compile(classModel);
        return compile.getConstructor(TypeReader.class).newInstance(typeReader);
    }
}
