package cc.jfire.dson;

import cc.jfire.baseutil.smc.compiler.CompileHelper;

import java.lang.reflect.Type;

public class Dson
{
    private static final DsonContext STANDARD_WRITER = new DsonContext();
    private static final DsonContext COMPILE_WRITER  = new DsonContext(new DsonConfig().setWriteUseCompile(true));
    private static final DsonContext STANDARD_READER = new DsonContext();
    private static final DsonContext COMPILE_READER  = new DsonContext(new DsonConfig().setReadUseCompile(true));

    public static  final CompileHelper DEFAULT_COMPILER_HELPER = new CompileHelper();

    public static String toJson(Object entity)
    {
        return STANDARD_WRITER.toJson(entity);
    }

    public static String toJsonByCompile(Object entity)
    {
        return COMPILE_WRITER.toJson(entity);
    }

    public static <T> T fromString(Type type, String str)
    {
        return STANDARD_READER.fromString(type, str);
    }

    public static <T> T fromStringByCompile(Type type, String str)
    {
        return COMPILE_READER.fromString(type, str);
    }

    public static Object fromString(String str)
    {
        return STANDARD_READER.fromString(Object.class, str);
    }

    public static Object fromStringByAttribute(String attribute, Type type, String str)
    {
        return STANDARD_READER.fromStringByAttribute(attribute, type, str);
    }
}
