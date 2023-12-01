package com.jfirer.dson.reader.impl;

import com.jfirer.dson.reader.JsonReader;
import com.jfirer.dson.reader.Stream;
import com.jfirer.dson.reader.TypeReader;
import com.jfirer.dson.reader.support.Entry;
import com.jfirer.dson.reader.support.Node;
import io.github.karlatemp.unsafeaccessor.UnsafeAccess;

import java.lang.reflect.Type;

public class ObjectReader implements TypeReader
{
    private Class ckass;
    private Node  rootNode;

    @Override
    public void init(Type type, JsonReader jsonReader)
    {
        this.ckass = (Class) type;
        rootNode   = Node.generateRoot(ckass, jsonReader, null);
    }

    @Override
    public Object fromString(Stream stream)
    {
        try
        {
            Object instance = UnsafeAccess.getInstance().getUnsafe().allocateInstance(ckass);
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
                    entry.setValue(instance, stream);
                }
                skipComma = stream.skipComma();
            }
            return instance;
        }
        catch (Exception e)
        {
            throw new IllegalStateException("当前出错的位置是:" + stream.errorPosition(), e);
        }
    }
}
