package cc.jfire.dson.reader.impl;

import cc.jfire.baseutil.reflect.ReflectUtil;
import cc.jfire.dson.DsonContext;
import cc.jfire.dson.reader.Stream;
import cc.jfire.dson.reader.TypeReader;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MapReader  implements TypeReader
{
    private TypeReader valueReader;
    private Class      ckass;

    @Override
    public void initialize(Type type, DsonContext dsonContext)
    {
        if (type instanceof Class)
        {
            ckass = (Class) type;
        }
        else if (type instanceof ParameterizedType)
        {
            ckass = (Class) ((ParameterizedType) type).getRawType();
            Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
            if (typeArguments[0] != String.class)
            {
                throw new IllegalArgumentException(type.toString());
            }
        }
        else
        {
            throw new IllegalArgumentException(type.toString());
        }
        if (ckass.isInterface())
        {
            ckass = HashMap.class;
        }
        if (type instanceof Class)
        {
            this.valueReader = dsonContext.parseReader(Object.class);
        }
        else if (type instanceof ParameterizedType)
        {
            Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
            this.valueReader = dsonContext.parseReader(typeArguments[1]);
        }
    }

    @Override
    public Object fromString(Stream stream)
    {
        TypeReader valueReader = this.valueReader;
        stream.startParseObject();
        try
        {
            Map map = (Map) ckass.newInstance();
            while (stream.parseObjectEnd() == false)
            {
                String name = stream.getName();
                stream.skipColon();
                if (stream.isNextNullAndSkip())
                {
                    ;
                }
                else
                {
                    map.put(name, valueReader.fromString(stream));
                }
                stream.skipComma();
            }
            return map;
        }
        catch (Exception e)
        {
            ReflectUtil.throwException(e);
            return null;
        }
    }
}
