package com.jfirer.dson.writer;

import com.jfirer.baseutil.STR;
import com.jfirer.baseutil.reflect.ReflectUtil;
import com.jfirer.baseutil.smc.SmcHelper;
import com.jfirer.baseutil.smc.compiler.CompileHelper;
import com.jfirer.baseutil.smc.model.ClassModel;
import com.jfirer.baseutil.smc.model.FieldModel;
import com.jfirer.baseutil.smc.model.MethodModel;
import com.jfirer.dson.DsonContext;
import com.jfirer.dson.util.JsonRenameStrategy;
import com.jfirer.dson.writer.impl.ObjectWriter;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public interface TypeWriter
{
    default void initialize(Type type, DsonContext contexts)
    {
    }

    /**
     * 将对象json输出到output中
     *
     * @param entity
     * @param output
     * @return
     */
    void toJson(Object entity, StringBuilder output);

    static ObjectWriter standard()
    {
        return new ObjectWriter();
    }

    @SneakyThrows
    static TypeWriter compile(Type type)
    {
        ClassModel classModel = new ClassModel(STR.format("CompileObjectWriter_{}", CompileHelper.COMPILE_COUNTER.getAndIncrement()));
        classModel.addInterface(TypeWriter.class);
        classModel.addField(new FieldModel("dsonContext", DsonContext.class, classModel));
        Class              ckazz = (Class) type;
        Map<String, Field> map   = new HashMap<>();
        while (ckazz != Object.class)
        {
            for (Field each : ckazz.getDeclaredFields())
            {
                map.putIfAbsent(JsonRenameStrategy.helpGetRename(each), each);
            }
            ckazz = ckazz.getSuperclass();
        }
        MethodModel initMethod = new MethodModel(TypeWriter.class.getDeclaredMethod("initialize", Type.class, DsonContext.class), classModel);
        initMethod.setParamterNames( "type","dsonContext");
        StringBuilder initBody = new StringBuilder();
        initBody.append("this.dsonContext = dsonContext;\r\n");
        MethodModel toJsonMethod = new MethodModel(TypeWriter.class.getDeclaredMethod("toJson", Object.class, StringBuilder.class), classModel);
        toJsonMethod.setParamterNames("instance", "builder");
        StringBuilder toJsonBody = new StringBuilder("builder.append(\"{\");\r\n");
        toJsonBody.append("boolean hasOutput = false;\r\n");
        String  referenceName = SmcHelper.getReferenceName(((Class<?>) type), classModel);
        boolean hasPrimitive  = false;
        for (Map.Entry<String, Field> each : map.entrySet())
        {
            if (Modifier.isFinal(each.getValue().getModifiers()) || Modifier.isStatic(each.getValue().getModifiers()))
            {
                continue;
            }
            int    classId    = ReflectUtil.getClassId(each.getValue().getType());
            String methodName = ReflectUtil.parseBeanGetMethodName(each.getValue());
            if (each.getValue().isAnnotationPresent(SerializeDefinition.class))
            {
                SerializeDefinition annotation = each.getValue().getAnnotation(SerializeDefinition.class);
                String              fieldname  = "typeWrite_" + CompileHelper.COMPILE_COUNTER.getAndIncrement();
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
                                                     {} reference = (({})instance).{}();
                                                                 if (reference != null)
                                                                 {
                                                                     builder.append("\\"{}\\":");
                                                                     {}.toJson(reference, builder);
                                                                     builder.append(',');
                                                                     hasOutput = true;
                                                                 }
                                                     }
                                                     """, SmcHelper.getReferenceName(each.getValue().getType(), classModel), referenceName, methodName, each.getKey(), fieldname));
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
                                                                        builder.append((({})instance).{}());
                                                                        builder.append(',');
                                                             """, each.getKey(), referenceName, methodName));
                        hasPrimitive = true;
                    }
                    case ReflectUtil.PRIMITIVE_CHAR ->
                    {
                        toJsonBody.append(STR.format("""
                                                             builder.append("\\"{}\\":");
                                                                         builder.append('"').append((({})instance).{}());
                                                                         builder.append('"').append(',');
                                                             """, each.getKey(), referenceName, methodName));
                        hasPrimitive = true;
                    }
                    case ReflectUtil.CLASS_BOOL, ReflectUtil.CLASS_BYTE, ReflectUtil.CLASS_SHORT, ReflectUtil.CLASS_INT, ReflectUtil.CLASS_LONG, ReflectUtil.CLASS_FLOAT,
                         ReflectUtil.CLASS_DOUBLE -> toJsonBody.append(STR.format("""
                                                                                          {
                                                                                          {} reference = (({})instance).{}();
                                                                                          if (reference != null)
                                                                                                      {
                                                                                                          builder.append("\\"{}\\":");
                                                                                                          builder.append(reference);
                                                                                                          builder.append(',');
                                                                                                          hasOutput = true;
                                                                                                        }
                                                                                          }
                                                                                          """, SmcHelper.getReferenceName(each.getValue().getType(), classModel), referenceName, methodName, each.getKey()));
                    case ReflectUtil.CLASS_STRING, ReflectUtil.CLASS_CHAR -> toJsonBody.append(STR.format("""
                                                                                                                  {
                                                                                                                  {} reference = (({})instance).{}();
                                                                                                                  if (reference != null)
                                                                                                                              {
                                                                                                                                  builder.append("\\"{}\\":");
                                                                                                                                  builder.append('"').append(reference).append('"');
                                                                                                                                  builder.append(',');
                                                                                                                                  hasOutput = true;
                                                                                                                                }
                                                                                                                  }
                                                                                                                  """, SmcHelper.getReferenceName(each.getValue().getType(), classModel), referenceName, methodName, each.getKey()));
                    default ->
                    {
                        if (Modifier.isFinal(each.getValue().getModifiers()))
                        {
                            String fieldname = "typeWrite_" + CompileHelper.COMPILE_COUNTER.getAndIncrement();
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
                                                                 {} reference = (({})instance).{}();
                                                                             if (reference != null)
                                                                             {
                                                                                 builder.append("\\"{}\\":");
                                                                                 {}.toJson(reference, builder);
                                                                                 builder.append(',');
                                                                                 hasOutput = true;
                                                                             }
                                                                 }
                                                                 """, SmcHelper.getReferenceName(each.getValue().getType(), classModel), referenceName, methodName, each.getKey(), fieldname));
                        }
                        else
                        {
                            toJsonBody.append(STR.format("""
                                                                 {
                                                                 {} reference =(({})instance).{}();
                                                                             if (reference != null)
                                                                             {
                                                                                 builder.append("\\"{}\\":");
                                                                                 dsonContext.parseWriter(reference.getClass()).toJson(reference, builder);
                                                                                 builder.append(',');
                                                                                 hasOutput = true;
                                                                             }
                                                                 }
                                                                 """, SmcHelper.getReferenceName(each.getValue().getType(), classModel), referenceName, methodName, each.getKey()));
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
        classModel.putMethodModel(initMethod);
        classModel.putMethodModel(toJsonMethod);
        Class<TypeWriter> compile    = (Class<TypeWriter>) CompileHelper.DEFAULT_COMPILE_HELPER.compile(classModel);
        TypeWriter        typeWriter = compile.getConstructor().newInstance();
        return typeWriter;
    }
}
