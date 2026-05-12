package cc.jfire.dson.reader.support.entry;

import cc.jfire.baseutil.STR;
import cc.jfire.baseutil.reflect.ReflectUtil;
import cc.jfire.baseutil.smc.SmcHelper;
import cc.jfire.baseutil.smc.model.ClassModel;
import cc.jfire.baseutil.smc.model.FieldModel;
import cc.jfire.baseutil.smc.model.MethodModel;
import cc.jfire.dson.Dson;
import cc.jfire.dson.DsonContext;
import cc.jfire.dson.reader.DeSerializeDefinition;
import cc.jfire.dson.reader.Stream;
import cc.jfire.dson.reader.TypeReader;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

public interface ReadEntry
{
    void setValue(Object instance, Stream stream);

    /**
     * 这个entry的实际输入输出名称
     *
     * @return
     */
    String name();

    static ReadEntry standard(String name, Field field, DsonContext dsonContext, Map<TypeVariable<?>, Type> typeVariableContext)
    {
        return new StandardReadEntry(name, field, dsonContext,typeVariableContext);
    }

    @SneakyThrows
    static ReadEntry compile(Field field, DsonContext dsonContext, Map<TypeVariable<?>, Type> typeVariableContext)
    {
        TypeReader typeReader = null;
        if (field.isAnnotationPresent(DeSerializeDefinition.class))
        {
            DeSerializeDefinition annotation = field.getAnnotation(DeSerializeDefinition.class);
            try
            {
                typeReader = annotation.value().newInstance();
                typeReader.initialize(field.getGenericType(), dsonContext, typeVariableContext);
            }
            catch (Exception e)
            {
                ReflectUtil.throwException(e);
            }
        }
        else if (ReflectUtil.getClassId(field.getType()) > ReflectUtil.CLASS_STRING)
        {
            typeReader = dsonContext.parseReader(field.getGenericType(), typeVariableContext);
        }
        ClassModel classModel = new ClassModel(STR.format("ReadEntry_{}_{}", field.getName(), TypeReader.COMPILE_COUNTER.getAndIncrement()));
        classModel.addField(new FieldModel("typeReader", TypeReader.class, classModel));
        classModel.addField(new FieldModel("typeVariableContext", Map.class, classModel));
        classModel.addConstructor("this.typeReader=$0;this.typeVariableContext=$1;", TypeReader.class, Map.class);
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
                case ReflectUtil.PRIMITIVE_BYTE, ReflectUtil.CLASS_BYTE -> builder.append(STR.format("(({})instance).{}((Byte)typeReader.fromString(stream,typeVariableContext));", typeName, methodName));
                case ReflectUtil.PRIMITIVE_INT, ReflectUtil.CLASS_INT -> builder.append(STR.format("(({})instance).{}((Integer)typeReader.fromString(stream,typeVariableContext));", typeName, methodName));
                case ReflectUtil.PRIMITIVE_SHORT, ReflectUtil.CLASS_SHORT -> builder.append(STR.format("(({})instance).{}((Short)typeReader.fromString(stream,typeVariableContext));", typeName, methodName));
                case ReflectUtil.PRIMITIVE_LONG, ReflectUtil.CLASS_LONG -> builder.append(STR.format("(({})instance).{}((Long)typeReader.fromString(stream,typeVariableContext));", typeName, methodName));
                case ReflectUtil.PRIMITIVE_FLOAT, ReflectUtil.CLASS_FLOAT -> builder.append(STR.format("(({})instance).{}((Float)typeReader.fromString(stream,typeVariableContext));", typeName, methodName));
                case ReflectUtil.PRIMITIVE_DOUBLE,
                     ReflectUtil.CLASS_DOUBLE -> builder.append(STR.format("(({})instance).{}((Double)typeReader.fromString(stream,typeVariableContext));", typeName, methodName));
                case ReflectUtil.PRIMITIVE_BOOL, ReflectUtil.CLASS_BOOL -> builder.append(STR.format("(({})instance).{}((Boolean)typeReader.fromString(stream,typeVariableContext));", typeName, methodName));
                case ReflectUtil.PRIMITIVE_CHAR,
                     ReflectUtil.CLASS_CHAR -> builder.append(STR.format("(({})instance).{}((Character)typeReader.fromString(stream,typeVariableContext));", typeName, methodName));
                default -> builder.append(STR.format("(({})instance).{}(({})typeReader.fromString(stream,typeVariableContext));", typeName, methodName, SmcHelper.getReferenceName(field.getType(), classModel)));
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
        Class<ReadEntry> compile = (Class<ReadEntry>) Dson.DEFAULT_COMPILER_HELPER.compile(classModel);
        return compile.getConstructor(TypeReader.class, Map.class).newInstance(typeReader, typeVariableContext);
    }
}
