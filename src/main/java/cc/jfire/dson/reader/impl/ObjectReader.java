package cc.jfire.dson.reader.impl;

import cc.jfire.dson.reader.ReaderContext;
import cc.jfire.dson.reader.Stream;
import cc.jfire.dson.reader.TypeReader;
import cc.jfire.dson.reader.support.Node;
import cc.jfire.dson.reader.support.entry.ReadEntry;
import io.github.karlatemp.unsafeaccessor.Unsafe;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ObjectReader implements TypeReader
{
    private              Class                      ckass;
    private              Node                       rootNode;
    private static final Unsafe                     UNSAFE = Unsafe.getUnsafe();

    @Override
    public void initialize(Type type, ReaderContext readerContext)
    {
        this.ckass          = type instanceof Class<?> ? (Class<?>) type : type instanceof ParameterizedType ? (Class) ((ParameterizedType) type).getRawType() : null;
        rootNode            = Node.generateRoot(this.ckass, type, readerContext);
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
