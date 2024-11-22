package com.jfirer.dson.reader.impl;

import com.jfirer.dson.DsonContext;
import com.jfirer.dson.reader.Stream;
import com.jfirer.dson.reader.TypeReader;
import com.jfirer.dson.reader.support.Node;
import com.jfirer.dson.reader.support.entry.ReadEntry;
import io.github.karlatemp.unsafeaccessor.Unsafe;

import java.lang.reflect.Type;

public class ObjectReader  implements TypeReader
{
    private              Class  ckass;
    private              Node   rootNode;
    private static final Unsafe UNSAFE = Unsafe.getUnsafe();

    @Override
    public void initialize(Type type, DsonContext dsonContext)
    {
        this.ckass = (Class) type;
        rootNode   = Node.generateRoot(ckass, dsonContext);
    }

    @Override
    public Object fromString(Stream stream)
    {
        try
        {
            Object instance = UNSAFE.allocateInstance(ckass);
            stream.startParseObject();
            boolean skipComma = false;
            while (skipComma || stream.parseObjectEnd() == false)
            {
                ReadEntry readEntry = stream.getName(rootNode);
                stream.skipColon();
                if (readEntry == null)
                {
                    stream.skipWholeValue();
                }
                else if (stream.isNextNullAndSkip())
                {
                    ;
                }
                else
                {
                    readEntry.setValue(instance, stream);
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
