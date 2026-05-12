package cc.jfire.dson;

import cc.jfire.baseutil.smc.compiler.CompileHelper;
import cc.jfire.dson.reader.support.TypeResolver;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Dson
{
    private static final DsonContext                                     STANDARD_WRITER         = new DsonContext();
    private static final DsonContext                                     COMPILE_WRITER          = new DsonContext(new DsonConfig().setWriteUseCompile(true));
    private static final DsonContext                                     STANDARD_READER         = new DsonContext();
    private static final DsonContext                                     COMPILE_READER          = new DsonContext(new DsonConfig().setReadUseCompile(true));
    private static       ConcurrentMap<Type, ConcurrentMap<TypeVariable<?>, Type>> typeResolvers           = new ConcurrentHashMap<>();
    public static final  CompileHelper                                   DEFAULT_COMPILER_HELPER = new CompileHelper();

    public static String toJson(Object entity)
    {
        return STANDARD_WRITER.toJson(entity);
    }

    public static Object toJsonValue(Object entity)
    {
        return STANDARD_WRITER.toJsonValue(entity);
    }

    public static String toJsonByCompile(Object entity)
    {
        return COMPILE_WRITER.toJson(entity);
    }

    public static Object toJsonValueByCompile(Object entity)
    {
        return COMPILE_WRITER.toJsonValue(entity);
    }

    public static <T> T fromString(Type type, String str)
    {
        ConcurrentMap<TypeVariable<?>, Type> typeContext = typeResolvers.computeIfAbsent(type, k -> TypeResolver.resolveTypeArguments(k));
        return STANDARD_READER.fromString(type, str, typeContext);
    }

    public static <T> T fromStringByCompile(Type type, String str)
    {
        ConcurrentMap<TypeVariable<?>, Type> typeContext = typeResolvers.computeIfAbsent(type, k -> TypeResolver.resolveTypeArguments(type));
        return COMPILE_READER.fromString(type, str, typeContext);
    }

    public static Object fromString(String str)
    {
        ConcurrentMap<TypeVariable<?>, Type> typeContext = typeResolvers.computeIfAbsent(Object.class, k -> TypeResolver.resolveTypeArguments(Object.class));
        return STANDARD_READER.fromString(Object.class, str, typeContext);
    }

    public static Object fromStringByAttribute(String attribute, Type type, String str)
    {
        ConcurrentMap<TypeVariable<?>, Type> typeContext = typeResolvers.computeIfAbsent(type, k -> TypeResolver.resolveTypeArguments(type));
        return STANDARD_READER.fromStringByAttribute(attribute, type, str,typeContext);
    }
}
