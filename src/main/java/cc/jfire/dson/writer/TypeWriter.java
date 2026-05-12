package cc.jfire.dson.writer;

import cc.jfire.baseutil.STR;
import cc.jfire.baseutil.reflect.ReflectUtil;
import cc.jfire.baseutil.smc.SmcHelper;
import cc.jfire.baseutil.smc.model.ClassModel;
import cc.jfire.baseutil.smc.model.FieldModel;
import cc.jfire.baseutil.smc.model.MethodModel;
import cc.jfire.dson.Dson;
import cc.jfire.dson.DsonContext;
import cc.jfire.dson.dynamic.DynamicJsonValue;
import cc.jfire.dson.util.JsonRenameStrategy;
import cc.jfire.dson.writer.impl.ObjectWriter;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public interface TypeWriter
{
    default void initialize(Type type, DsonContext dsonContext)
    {
    }

    /**
     * 将对象json输出到output中
     *
     * @param entity 要序列化的对象
     * @param output 输出的StringBuilder
     */
    void toJson(Object entity, StringBuilder output);

    /**
     * 将对象转化为 Json 的形式，返回类型包括有：
     * 1、包装类型，如果入参是包装类型。
     * 2、Map，如果入参是一个复杂的对象。
     * 3、List，如果入参是一个数组或者集合
     * 4、null。入参为 null
     *
     * @param entity
     * @return
     */
    Object toJsonValue(Object entity);

    static ObjectWriter standard()
    {
        return new ObjectWriter();
    }

    AtomicInteger COMPILE_COUNTER = new AtomicInteger();

    @SneakyThrows
    static TypeWriter compile(Class ckass)
    {
        ClassModel classModel = new ClassModel(STR.format("CompileObjectWriter_{}", COMPILE_COUNTER.getAndIncrement()));
        classModel.addInterface(TypeWriter.class);
        classModel.addField(new FieldModel("dsonContext", DsonContext.class, classModel));
        Map<String, Field> map = new HashMap<>();
        for (Field each : ReflectUtil.findPojoBeanGetFields(ckass))
        {
            map.putIfAbsent(JsonRenameStrategy.helpGetRename(each), each);
        }
        MethodModel initMethod = new MethodModel(TypeWriter.class.getDeclaredMethod("initialize", Type.class, DsonContext.class), classModel);
        initMethod.setParamterNames("type", "dsonContext");
        StringBuilder initBody = new StringBuilder();
        initBody.append("this.dsonContext = dsonContext;\r\n");
        MethodModel toJsonMethod = new MethodModel(TypeWriter.class.getDeclaredMethod("toJson", Object.class, StringBuilder.class), classModel);
        toJsonMethod.setParamterNames("entity", "builder");
        String        referenceName = SmcHelper.getReferenceName((ckass), classModel);
        StringBuilder toJsonBody    = new StringBuilder("builder.append(\"{\");\r\n");
        toJsonBody.append(STR.format("{} instance = ({})entity;\r\n", referenceName, referenceName));
        toJsonBody.append("boolean hasOutput = false;\r\n");
        MethodModel toJsonValueMethod = new MethodModel(TypeWriter.class.getDeclaredMethod("toJsonValue", Object.class), classModel);
        toJsonValueMethod.setParamterNames("entity");
        StringBuilder toJsonValueBody = new StringBuilder();
        toJsonValueBody.append(STR.format("{} instance = ({})entity;\r\n", referenceName, referenceName));
        if (DynamicJsonValue.class.isAssignableFrom(ckass))
        {
            toJsonValueBody.append("return ((cc.jfire.dson.dynamic.DynamicJsonValue)instance).toJsonValue();");
        }
        else
        {
            toJsonValueBody.append("java.util.Map map = new java.util.HashMap();\r\n");
            for (Map.Entry<String, Field> each : map.entrySet())
            {
                int    classId    = ReflectUtil.getClassId(each.getValue().getType());
                String methodName = ReflectUtil.parseBeanGetMethodName(each.getValue());
                switch (classId)
                {
                    case ReflectUtil.PRIMITIVE_INT, ReflectUtil.PRIMITIVE_LONG, ReflectUtil.PRIMITIVE_FLOAT, ReflectUtil.PRIMITIVE_DOUBLE, ReflectUtil.PRIMITIVE_SHORT,
                         ReflectUtil.PRIMITIVE_BYTE, ReflectUtil.PRIMITIVE_BOOL, ReflectUtil.PRIMITIVE_CHAR ->
                    {
                        toJsonValueBody.append(STR.format("""
                                                                  map.put("{}",instance.{}());
                                                                  """, each.getKey(), methodName));
                    }
                    case ReflectUtil.CLASS_BOOL, ReflectUtil.CLASS_BYTE, ReflectUtil.CLASS_SHORT, ReflectUtil.CLASS_INT, ReflectUtil.CLASS_LONG, ReflectUtil.CLASS_FLOAT,
                         ReflectUtil.CLASS_DOUBLE, ReflectUtil.CLASS_STRING, ReflectUtil.CLASS_CHAR -> toJsonValueBody.append(STR.format("""
                                                                                                                                                 {
                                                                                                                                                 {} reference = instance.{}();
                                                                                                                                                 if (reference != null)
                                                                                                                                                             {
                                                                                                                                                                 map.put("{}",reference);
                                                                                                                                                               }
                                                                                                                                                 }
                                                                                                                                                 """, SmcHelper.getReferenceName(each.getValue().getType(), classModel), methodName, each.getKey()));
                    default ->
                    {
                        if (Modifier.isFinal(each.getValue().getType().getModifiers()))
                        {
                            String fieldname = "typeWrite_" + COMPILE_COUNTER.getAndIncrement();
                            classModel.addField(new FieldModel(fieldname, TypeWriter.class, classModel));
                            classModel.addImport(each.getValue().getType());
                            classModel.addImport(Field.class);
                            classModel.addImport(Throwable.class);
                            initBody.append(STR.format("""
                                                               {
                                                               try{
                                                                   Field field = {}.class.getDeclaredField("{}");
                                                                   {}  = dsonContext.parseWriter(field.getGenericType());
                                                                   }catch(Throwable e){;}
                                                               }
                                                               """, SmcHelper.getReferenceName(each.getValue().getDeclaringClass(), classModel), each.getValue().getName(), fieldname));
                            toJsonValueBody.append(STR.format("""
                                                                      {
                                                                      {} reference = instance.{}();
                                                                                  if (reference != null)
                                                                                  {
                                                                                      map.put("{}",{}.toJsonValue(reference));
                                                                                  }
                                                                      }
                                                                      """, SmcHelper.getReferenceName(each.getValue().getType(), classModel), methodName, each.getKey(), fieldname));
                        }
                        else
                        {
                            toJsonValueBody.append(STR.format("""
                                                                      {
                                                                      {} reference =instance.{}();
                                                                                  if (reference != null)
                                                                                  {
                                                                                   map.put("{}",dsonContext.parseWriter(reference.getClass()).toJsonValue(reference));
                                                                                  }
                                                                      }
                                                                      """, SmcHelper.getReferenceName(each.getValue().getType(), classModel), methodName, each.getKey()));
                        }
                    }
                }
            }
            toJsonValueBody.append("return map;");
        }
        boolean hasPrimitive = false;
        for (Map.Entry<String, Field> each : map.entrySet())
        {
            int    classId    = ReflectUtil.getClassId(each.getValue().getType());
            String methodName = ReflectUtil.parseBeanGetMethodName(each.getValue());
            if (each.getValue().isAnnotationPresent(SerializeDefinition.class))
            {
                SerializeDefinition annotation = each.getValue().getAnnotation(SerializeDefinition.class);
                String              fieldname  = "typeWrite_" + COMPILE_COUNTER.getAndIncrement();
                classModel.addField(new FieldModel(fieldname, TypeWriter.class, classModel));
                classModel.addImport(annotation.value());
                classModel.addImport(Field.class);
                initBody.append(STR.format("""
                                                   {
                                                   try{
                                                       Field field = {}.class.getDeclaredField("{}");
                                                       {}  = new {}();
                                                       {}.initialize(field.getGenericType(),dsonContext);
                                                       }catch(Throwable e){;}
                                                   }
                                                   """, SmcHelper.getReferenceName(each.getValue().getDeclaringClass(), classModel), each.getValue().getName(), fieldname, SmcHelper.getReferenceName(annotation.value(), classModel), fieldname));
                toJsonBody.append(STR.format("""
                                                     {
                                                     {} reference = instance.{}();
                                                                 if (reference != null)
                                                                 {
                                                                     builder.append("\\"{}\\":");
                                                                     {}.toJson(reference, builder);
                                                                     builder.append(',');
                                                                     hasOutput = true;
                                                                 }
                                                     }
                                                     """, SmcHelper.getReferenceName(each.getValue().getType(), classModel), methodName, each.getKey(), fieldname));
            }
            else
            {
                switch (classId)
                {
                    case ReflectUtil.PRIMITIVE_INT, ReflectUtil.PRIMITIVE_LONG, ReflectUtil.PRIMITIVE_FLOAT, ReflectUtil.PRIMITIVE_DOUBLE, ReflectUtil.PRIMITIVE_SHORT,
                         ReflectUtil.PRIMITIVE_BYTE, ReflectUtil.PRIMITIVE_BOOL ->
                    {
                        toJsonBody.append(STR.format("""
                                                             builder.append("\\"{}\\":");
                                                                        builder.append(instance.{}());
                                                                        builder.append(',');
                                                             """, each.getKey(), methodName));
                        hasPrimitive = true;
                    }
                    case ReflectUtil.PRIMITIVE_CHAR ->
                    {
                        toJsonBody.append(STR.format("""
                                                             builder.append("\\"{}\\":");
                                                                         builder.append('"').append(instance.{}());
                                                                         builder.append('"').append(',');
                                                             """, each.getKey(), methodName));
                        hasPrimitive = true;
                    }
                    case ReflectUtil.CLASS_BOOL, ReflectUtil.CLASS_BYTE, ReflectUtil.CLASS_SHORT, ReflectUtil.CLASS_INT, ReflectUtil.CLASS_LONG, ReflectUtil.CLASS_FLOAT,
                         ReflectUtil.CLASS_DOUBLE -> toJsonBody.append(STR.format("""
                                                                                          {
                                                                                          {} reference = instance.{}();
                                                                                          if (reference != null)
                                                                                                      {
                                                                                                          builder.append("\\"{}\\":");
                                                                                                          builder.append(reference);
                                                                                                          builder.append(',');
                                                                                                          hasOutput = true;
                                                                                                        }
                                                                                          }
                                                                                          """, SmcHelper.getReferenceName(each.getValue().getType(), classModel), methodName, each.getKey()));
                    case ReflectUtil.CLASS_STRING -> toJsonBody.append(STR.format("""
                                                                                                           {
                                                                                                           {} reference = instance.{}();
                                                                                                           if (reference != null)
                                                                                                                       {
                                                                                                                           builder.append("\\"{}\\":");
                                                                                                                           builder.append('"');
                                                                                                                           cc.jfire.dson.util.WriterUtil.writeString(builder, reference);
                                                                                                                           builder.append('"');
                                                                                                                           builder.append(',');
                                                                                                                           hasOutput = true;
                                                                                                                         }
                                                                                                           }
                                                                                                           """, SmcHelper.getReferenceName(each.getValue().getType(), classModel), methodName, each.getKey()));
                    case ReflectUtil.CLASS_CHAR -> toJsonBody.append(STR.format("""
                                                                                                         {
                                                                                                         {} reference = instance.{}();
                                                                                                         if (reference != null)
                                                                                                                     {
                                                                                                                         builder.append("\\"{}\\":");
                                                                                                                         builder.append('"').append(reference).append('"');
                                                                                                                         builder.append(',');
                                                                                                                         hasOutput = true;
                                                                                                                       }
                                                                                                         }
                                                                                                         """, SmcHelper.getReferenceName(each.getValue().getType(), classModel), methodName, each.getKey()));
                    default ->
                    {
                        if (Modifier.isFinal(each.getValue().getType().getModifiers()))
                        {
                            String fieldname = "typeWrite_" + COMPILE_COUNTER.getAndIncrement();
                            classModel.addField(new FieldModel(fieldname, TypeWriter.class, classModel));
                            classModel.addImport(each.getValue().getType());
                            classModel.addImport(Field.class);
                            classModel.addImport(Throwable.class);
                            initBody.append(STR.format("""
                                                               {
                                                               try{
                                                                   Field field = {}.class.getDeclaredField("{}");
                                                                   {}  = dsonContext.parseWriter(field.getGenericType());
                                                                   }catch(Throwable e){;}
                                                               }
                                                               """, SmcHelper.getReferenceName(each.getValue().getDeclaringClass(), classModel), each.getValue().getName(), fieldname));
                            toJsonBody.append(STR.format("""
                                                                 {
                                                                 {} reference = instance.{}();
                                                                             if (reference != null)
                                                                             {
                                                                                 builder.append("\\"{}\\":");
                                                                                 {}.toJson(reference, builder);
                                                                                 builder.append(',');
                                                                                 hasOutput = true;
                                                                             }
                                                                 }
                                                                 """, SmcHelper.getReferenceName(each.getValue().getType(), classModel), methodName, each.getKey(), fieldname));
                        }
                        else
                        {
                            toJsonBody.append(STR.format("""
                                                                 {
                                                                 {} reference =instance.{}();
                                                                             if (reference != null)
                                                                             {
                                                                                 builder.append("\\"{}\\":");
                                                                                 dsonContext.parseWriter(reference.getClass()).toJson(reference, builder);
                                                                                 builder.append(',');
                                                                                 hasOutput = true;
                                                                             }
                                                                 }
                                                                 """, SmcHelper.getReferenceName(each.getValue().getType(), classModel), methodName, each.getKey()));
                        }
                    }
                }
            }
        }
        if (hasPrimitive)
        {
            toJsonBody.append("""
                                      builder.setLength(builder.length()-1);
                                      builder.append('}');
                                      """);
        }
        else
        {
            toJsonBody.append("""
                                      if(hasOutput){
                                      builder.setLength(builder.length()-1);
                                      }
                                      builder.append('}');
                                      """);
        }
        initMethod.setBody(initBody.toString());
        toJsonMethod.setBody(toJsonBody.toString());
        toJsonValueMethod.setBody(toJsonValueBody.toString());
        classModel.putMethodModel(initMethod);
        classModel.putMethodModel(toJsonMethod);
        classModel.putMethodModel(toJsonValueMethod);
        Class<TypeWriter> compile    = (Class<TypeWriter>) Dson.DEFAULT_COMPILER_HELPER.compile(classModel);
        TypeWriter        typeWriter = compile.getConstructor().newInstance();
        return typeWriter;
    }
}
