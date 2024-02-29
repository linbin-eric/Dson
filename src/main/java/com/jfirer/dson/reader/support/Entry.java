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
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class Entry
{
    protected            String                     name;
    public               TypeReader                 typeReader;
    public               PrimitiveType              primitiveType;
    public               ValueAccessor              valueAccessor;
    private static final AtomicInteger              nameCount = new AtomicInteger();
    private              BiConsumer<Object, Object> consumer;
    private              BiIntConsumer<Object>      biIntConsumer;
    private              BiByteConsumer<Object>     biByteConsumer;
    private              BiShortConsumer<Object>    biShortConsumer;
    private              BiCharConsumer<Object>     biCharConsumer;
    private              BiFloatConsumer<Object>    biFloatConsumer;
    private              BiDoubleConsumer<Object>   biDoubleConsumer;
    private              BiLongConsumer<Object>     biLongConsumer;
    private              BiBooleanConsumer<Object>  biBooleanConsumer;

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

    interface BiIntConsumer<T>
    {
        void accept(T t, int i);
    }

    interface BiDoubleConsumer<T>
    {
        void accept(T t, double i);
    }

    interface BiByteConsumer<T>
    {
        void accept(T t, byte i);
    }

    interface BiCharConsumer<T>
    {
        void accept(T t, char i);
    }

    interface BiShortConsumer<T>
    {
        void accept(T t, short i);
    }

    interface BiFloatConsumer<T>
    {
        void accept(T t, float i);
    }

    interface BiLongConsumer<T>
    {
        void accept(T t, long i);
    }

    interface BiBooleanConsumer<T>
    {
        void accept(T t, boolean i);
    }

    public Entry(Field field, JsonReader jsonReader)
    {
        name = field.getName();
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try
        {
            MethodType interfaceType;
            Class<?>   fieldType = field.getType();
            if (fieldType == int.class || fieldType == Integer.class)
            {
                primitiveType = PrimitiveType.INT;
                biIntConsumer = (BiIntConsumer<Object>) LambdaMetafactory.metafactory(//
                                                                                      lookup,//固定参数
                                                                                      "accept",//需要实现的函数式接口的方法名
                                                                                      MethodType.methodType(BiIntConsumer.class),//固定写法，中间参数是需要实现的函数接口类
                                                                                      MethodType.methodType(void.class, Object.class, int.class),//前面的apple的方法签名
                                                                                      lookup.findVirtual(field.getDeclaringClass(), "set" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1), MethodType.methodType(void.class, field.getType())),//这个函数接口需要引用的类的实例方法
                                                                                      MethodType.methodType(void.class, field.getDeclaringClass(), int.class)//这个函数式接口实际实现的时候，方法签名。对比前前一个，这个方法签名是将泛型的信息提供出来了，前面那个泛型的信息都被抹掉了
                                                                                     ).getTarget().invoke();
            }
            else if (fieldType == char.class || fieldType == Character.class)
            {
                primitiveType = PrimitiveType.CHAR;
                biCharConsumer = (BiCharConsumer<Object>) LambdaMetafactory.metafactory(//
                                                                                        lookup,//固定参数
                                                                                        "accept",//需要实现的函数式接口的方法名
                                                                                        MethodType.methodType(BiCharConsumer.class),//固定写法，中间参数是需要实现的函数接口类
                                                                                        MethodType.methodType(void.class, Object.class, char.class),//前面的apple的方法签名
                                                                                        lookup.findVirtual(field.getDeclaringClass(), "set" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1), MethodType.methodType(void.class, field.getType())),//这个函数接口需要引用的类的实例方法
                                                                                        MethodType.methodType(void.class, field.getDeclaringClass(), char.class)//这个函数式接口实际实现的时候，方法签名。对比前前一个，这个方法签名是将泛型的信息提供出来了，前面那个泛型的信息都被抹掉了
                                                                                       ).getTarget().invoke();
            }
            else if (fieldType == long.class || fieldType == Long.class)
            {
                primitiveType = PrimitiveType.LONG;
                biLongConsumer = (BiLongConsumer<Object>) LambdaMetafactory.metafactory(//
                                                                                        lookup,//固定参数
                                                                                        "accept",//需要实现的函数式接口的方法名
                                                                                        MethodType.methodType(BiLongConsumer.class),//固定写法，中间参数是需要实现的函数接口类
                                                                                        MethodType.methodType(void.class, Object.class, long.class),//前面的apple的方法签名
                                                                                        lookup.findVirtual(field.getDeclaringClass(), "set" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1), MethodType.methodType(void.class, field.getType())),//这个函数接口需要引用的类的实例方法
                                                                                        MethodType.methodType(void.class, field.getDeclaringClass(), long.class)//这个函数式接口实际实现的时候，方法签名。对比前前一个，这个方法签名是将泛型的信息提供出来了，前面那个泛型的信息都被抹掉了
                                                                                       ).getTarget().invoke();
            }
            else if (fieldType == short.class || fieldType == Short.class)
            {
                primitiveType = PrimitiveType.SHORT;
                biShortConsumer = (BiShortConsumer<Object>) LambdaMetafactory.metafactory(//
                                                                                          lookup,//固定参数
                                                                                          "accept",//需要实现的函数式接口的方法名
                                                                                          MethodType.methodType(BiShortConsumer.class),//固定写法，中间参数是需要实现的函数接口类
                                                                                          MethodType.methodType(void.class, Object.class, short.class),//前面的apple的方法签名
                                                                                          lookup.findVirtual(field.getDeclaringClass(), "set" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1), MethodType.methodType(void.class, field.getType())),//这个函数接口需要引用的类的实例方法
                                                                                          MethodType.methodType(void.class, field.getDeclaringClass(), short.class)//这个函数式接口实际实现的时候，方法签名。对比前前一个，这个方法签名是将泛型的信息提供出来了，前面那个泛型的信息都被抹掉了
                                                                                         ).getTarget().invoke();
            }
            else if (fieldType == byte.class || fieldType == Byte.class)
            {
                primitiveType = PrimitiveType.BYTE;
                biByteConsumer = (BiByteConsumer<Object>) LambdaMetafactory.metafactory(//
                                                                                        lookup,//固定参数
                                                                                        "accept",//需要实现的函数式接口的方法名
                                                                                        MethodType.methodType(BiByteConsumer.class),//固定写法，中间参数是需要实现的函数接口类
                                                                                        MethodType.methodType(void.class, Object.class, byte.class),//前面的apple的方法签名
                                                                                        lookup.findVirtual(field.getDeclaringClass(), "set" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1), MethodType.methodType(void.class, field.getType())),//这个函数接口需要引用的类的实例方法
                                                                                        MethodType.methodType(void.class, field.getDeclaringClass(), byte.class)//这个函数式接口实际实现的时候，方法签名。对比前前一个，这个方法签名是将泛型的信息提供出来了，前面那个泛型的信息都被抹掉了
                                                                                       ).getTarget().invoke();
            }
            else if (fieldType == boolean.class || fieldType == Boolean.class)
            {
                primitiveType = PrimitiveType.BOOL;
                biBooleanConsumer = (BiBooleanConsumer<Object>) LambdaMetafactory.metafactory(//
                                                                                              lookup,//固定参数
                                                                                              "accept",//需要实现的函数式接口的方法名
                                                                                              MethodType.methodType(BiBooleanConsumer.class),//固定写法，中间参数是需要实现的函数接口类
                                                                                              MethodType.methodType(void.class, Object.class, boolean.class),//前面的apple的方法签名
                                                                                              lookup.findVirtual(field.getDeclaringClass(), "set" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1), MethodType.methodType(void.class, field.getType())),//这个函数接口需要引用的类的实例方法
                                                                                              MethodType.methodType(void.class, field.getDeclaringClass(), boolean.class)//这个函数式接口实际实现的时候，方法签名。对比前前一个，这个方法签名是将泛型的信息提供出来了，前面那个泛型的信息都被抹掉了
                                                                                             ).getTarget().invoke();
            }
            else if (fieldType == float.class || fieldType == Float.class)
            {
                primitiveType = PrimitiveType.FLOAT;
                biFloatConsumer = (BiFloatConsumer<Object>) LambdaMetafactory.metafactory(//
                                                                                          lookup,//固定参数
                                                                                          "accept",//需要实现的函数式接口的方法名
                                                                                          MethodType.methodType(BiFloatConsumer.class),//固定写法，中间参数是需要实现的函数接口类
                                                                                          MethodType.methodType(void.class, Object.class, float.class),//前面的apple的方法签名
                                                                                          lookup.findVirtual(field.getDeclaringClass(), "set" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1), MethodType.methodType(void.class, field.getType())),//这个函数接口需要引用的类的实例方法
                                                                                          MethodType.methodType(void.class, field.getDeclaringClass(), float.class)//这个函数式接口实际实现的时候，方法签名。对比前前一个，这个方法签名是将泛型的信息提供出来了，前面那个泛型的信息都被抹掉了
                                                                                         ).getTarget().invoke();
            }
            else if (fieldType == double.class || fieldType == Double.class)
            {
                primitiveType = PrimitiveType.DOUBLE;
                biDoubleConsumer = (BiDoubleConsumer<Object>) LambdaMetafactory.metafactory(//
                                                                                            lookup,//固定参数
                                                                                            "accept",//需要实现的函数式接口的方法名
                                                                                            MethodType.methodType(BiDoubleConsumer.class),//固定写法，中间参数是需要实现的函数接口类
                                                                                            MethodType.methodType(void.class, Object.class, double.class),//前面的apple的方法签名
                                                                                            lookup.findVirtual(field.getDeclaringClass(), "set" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1), MethodType.methodType(void.class, field.getType())),//这个函数接口需要引用的类的实例方法
                                                                                            MethodType.methodType(void.class, field.getDeclaringClass(), double.class)//这个函数式接口实际实现的时候，方法签名。对比前前一个，这个方法签名是将泛型的信息提供出来了，前面那个泛型的信息都被抹掉了
                                                                                           ).getTarget().invoke();
            }
            else
            {
                interfaceType = MethodType.methodType(void.class, Object.class, Object.class);
                consumer = (BiConsumer<Object, Object>) LambdaMetafactory.metafactory(//
                                                                                      lookup,//固定参数
                                                                                      "accept",//需要实现的函数式接口的方法名
                                                                                      MethodType.methodType(BiConsumer.class),//固定写法，中间参数是需要实现的函数接口类
                                                                                      interfaceType,//前面的apple的方法签名
                                                                                      lookup.findVirtual(field.getDeclaringClass(), "set" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1), MethodType.methodType(void.class, field.getType())),//这个函数接口需要引用的类的实例方法
                                                                                      MethodType.methodType(void.class, field.getDeclaringClass(), field.getType())//这个函数式接口实际实现的时候，方法签名。对比前前一个，这个方法签名是将泛型的信息提供出来了，前面那个泛型的信息都被抹掉了
                                                                                     ).getTarget().invoke();
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        Class<?> fieldType = field.getType();
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

    public Entry(String name, Field field, JsonReader jsonReader)
    {
        this.name = name;
        valueAccessor = new ValueAccessor(field);
        Class fieldType = field.getType();
        if (field.isAnnotationPresent(DeSerializeDefinition.class))
        {
            primitiveType = PrimitiveType.NO;
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
        else if (fieldType == int.class || fieldType == Integer.class)
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
            typeReader = jsonReader.get(field.getGenericType());
        }
    }

    public void setValueByLambda(Object instance, Stream stream)
    {
        switch (primitiveType)
        {
            case INT -> biIntConsumer.accept(instance, stream.getInt());
            case BOOL -> biBooleanConsumer.accept(instance, stream.getBoolean());
            case CHAR -> biCharConsumer.accept(instance, stream.getChar());
            case BYTE -> biByteConsumer.accept(instance, stream.getByte());
            case SHORT -> biShortConsumer.accept(instance, stream.getShort());
            case LONG -> biLongConsumer.accept(instance, stream.getLong());
            case FLOAT -> biFloatConsumer.accept(instance, stream.getFloat());
            case DOUBLE -> biDoubleConsumer.accept(instance, stream.getDouble());
            case STRING -> consumer.accept(instance, stream.getStringValue());
            case NO -> consumer.accept(instance, typeReader.fromString(stream));
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

    @Override
    public String toString()
    {
        return name;
    }
}
