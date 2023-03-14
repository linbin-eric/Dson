package com.jfirer.dson.reader.impl;

import com.jfirer.baseutil.reflect.ReflectUtil;
import com.jfirer.dson.reader.JsonReader;
import com.jfirer.dson.reader.Stream;
import com.jfirer.dson.reader.TypeReader;
import com.jfirer.dson.reader.support.Entry;
import com.jfirer.dson.reader.support.Node;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Type;
import java.util.function.Supplier;

public class LambdaObjectReader implements TypeReader
{
    private Class       ckass;
    private Node        rootNode;
    private Supplier<?> supplier;

    @Override
    public void init(Type type, JsonReader jsonReader)
    {
        this.ckass = (Class) type;
        rootNode = Node.generateLambdaRoot(ckass, jsonReader);
        try
        {
            supplier = getSupplier(ckass);
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
    }

    private Supplier<?> getSupplier(Class<?> ckass) throws Throwable
    {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        return (Supplier<?>) LambdaMetafactory.metafactory(//
                                                           lookup,//固定参数
                                                           "get",//需要实现的函数式接口的方法名
                                                           MethodType.methodType(Supplier.class),//固定写法，中间参数是需要实现的函数接口类
                                                           MethodType.methodType(Object.class),//前面的apple的方法签名
                                                           lookup.findConstructor(ckass, MethodType.methodType(void.class)),//这个函数接口需要引用的类的实例方法
                                                           MethodType.methodType(ckass)//这个函数式接口实际实现的时候，方法签名。对比前前一个，这个方法签名是将泛型的信息提供出来了，前面那个泛型的信息都被抹掉了
                                                          ).getTarget().invoke();
    }

    @Override
    public Object fromString(Stream stream)
    {
        try
        {
            Object instance = supplier.get();
            stream.startParseObject();
            boolean skipComma = false;
            while (skipComma || stream.parseObjectEnd() == false)
            {
                Entry entry = stream.getName(rootNode);
                stream.skipColon();
                if (entry == null)
                {
                    stream.skipWholeValue();
                }
                else
                {
                    entry.setValueByLambda(instance, stream);
                }
                skipComma = stream.skipComma();
            }
            return instance;
        }
        catch (Exception e)
        {
            ReflectUtil.throwException(e);
            return null;
        }
    }
}
