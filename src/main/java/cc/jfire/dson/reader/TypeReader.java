package cc.jfire.dson.reader;

import cc.jfire.baseutil.STR;
import cc.jfire.baseutil.reflect.ReflectUtil;
import cc.jfire.baseutil.smc.SmcHelper;
import cc.jfire.baseutil.smc.model.ClassModel;
import cc.jfire.baseutil.smc.model.FieldModel;
import cc.jfire.baseutil.smc.model.MethodModel;
import cc.jfire.dson.Dson;
import cc.jfire.dson.DsonContext;
import cc.jfire.dson.reader.support.Node;
import cc.jfire.dson.reader.support.entry.ReadEntry;
import cc.jfire.dson.util.JsonRenameStrategy;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public interface TypeReader
{
    default void initialize(Type type, DsonContext dsonContext, Map<TypeVariable<?>, Type> typeVariableContext)
    {
    }

    Object fromString(Stream stream, Map<TypeVariable<?>, Type> typeVariableContext);

    AtomicInteger COMPILE_COUNTER = new AtomicInteger();

    @SneakyThrows
    static TypeReader compile(Class ckazz)
    {
        ClassModel classModel = new ClassModel("BeanReader_" + COMPILE_COUNTER.getAndIncrement());
        classModel.addInterface(TypeReader.class);
        classModel.addImport(Node.class);
        classModel.addImport(Stream.class);
        classModel.addImport(ReadEntry.class);
        classModel.addField(new FieldModel("rootNode", Node.class, classModel));
        classModel.addField(new FieldModel("readerTypeVariableContext", Map.class, classModel));
        StringBuilder builder       = new StringBuilder();
        String        referenceName = SmcHelper.getReferenceName(ckazz, classModel);
        MethodModel   initMethod    = new MethodModel(TypeReader.class.getDeclaredMethod("initialize", Type.class, DsonContext.class, Map.class), classModel);
        initMethod.setParamterNames("type", "dsonContext", "typeVariableContext");
        StringBuilder initBody = new StringBuilder(STR.format("""
                java.util.Map thisTypeVariableContext = cc.jfire.dson.reader.support.TypeResolver.resolveTypeArguments(type);
                thisTypeVariableContext.putAll(typeVariableContext);
                readerTypeVariableContext = thisTypeVariableContext;
                rootNode   = Node.generateRoot({}.class, type, dsonContext, thisTypeVariableContext);
                """, referenceName));
        MethodModel   fromString = new MethodModel(TypeReader.class.getDeclaredMethod("fromString", Stream.class, Map.class), classModel);
        fromString.setParamterNames("stream", "typeVariableContext");
        builder.append(referenceName).append(" instance = new ").append(referenceName).append("();\r\n");
        builder.append("stream.startParseObject();\r\n");
        builder.append("boolean skipComma = false;\r\n");
        Field[]       fields     = ReflectUtil.findPojoBeanSetFields(ckazz);
        StringBuilder setContent = new StringBuilder();
        setContent.append("switch(readEntry.name())\r\n{\r\n");
        classModel.addImport(DeSerializeDefinition.class);
        for (Field each : fields)
        {
            String content;
            String declaringClassName = SmcHelper.getReferenceName(each.getDeclaringClass(), classModel);
            String setterTarget       = STR.format("(({})instance)", declaringClassName);
            String methodName         = ReflectUtil.parseBeanSetMethodName(each);
            if (each.isAnnotationPresent(DeSerializeDefinition.class))
            {
                String typeReaderName = "typeReader_" + COMPILE_COUNTER.getAndIncrement();
                classModel.addField(new FieldModel(typeReaderName, TypeReader.class, classModel));
                initBody.append("try\r\n{\r\n");
                initBody.append("java.lang.reflect.Field field = " + declaringClassName + ".class.getDeclaredField(\"" + each.getName() + "\");\r\n");
                initBody.append(typeReaderName + "=field.getAnnotation(DeSerializeDefinition.class).value().newInstance();\r\n");
                initBody.append(typeReaderName + ".initialize(field.getGenericType(),dsonContext,thisTypeVariableContext);\r\n");
                initBody.append("}\r\ncatch(Throwable e){;}\r\n");
                content = STR.format("{}.{}(({}){}.fromString(stream,readerTypeVariableContext));", setterTarget, methodName, SmcHelper.getReferenceName(each.getType(), classModel), typeReaderName);
            }
            else if (ReflectUtil.isNonBoxedObject(each.getType()) && each.getType() != String.class)
            {
                String typeReaderName = "typeReader_" + COMPILE_COUNTER.getAndIncrement();
                classModel.addField(new FieldModel(typeReaderName, TypeReader.class, classModel));
                initBody.append("try\r\n{\r\n");
                initBody.append("java.lang.reflect.Field field = " + declaringClassName + ".class.getDeclaredField(\"" + each.getName() + "\");\r\n");
                initBody.append(typeReaderName + "=dsonContext.parseReader(field.getGenericType(),thisTypeVariableContext);\r\n");
                initBody.append("}\r\ncatch(Throwable e){;}\r\n");
                content = STR.format("{}.{}(({}){}.fromString(stream,readerTypeVariableContext));", setterTarget, methodName, SmcHelper.getReferenceName(each.getType(), classModel), typeReaderName);
            }
            else
            {
                int classId = ReflectUtil.getClassId(each.getType());
                content = switch (classId)
                {
                    case ReflectUtil.PRIMITIVE_INT -> setterTarget + "." + methodName + "(stream.getInt());";
                    case ReflectUtil.PRIMITIVE_LONG -> setterTarget + "." + methodName + "(stream.getLong());";
                    case ReflectUtil.PRIMITIVE_FLOAT -> setterTarget + "." + methodName + "(stream.getFloat());";
                    case ReflectUtil.PRIMITIVE_DOUBLE -> setterTarget + "." + methodName + "(stream.getDouble());";
                    case ReflectUtil.PRIMITIVE_BOOL, ReflectUtil.CLASS_BOOL -> setterTarget + "." + methodName + "(stream.getBoolean());";
                    case ReflectUtil.PRIMITIVE_BYTE -> setterTarget + "." + methodName + "(stream.getByte());";
                    case ReflectUtil.PRIMITIVE_SHORT -> setterTarget + "." + methodName + "(stream.getShort());";
                    case ReflectUtil.PRIMITIVE_CHAR, ReflectUtil.CLASS_CHAR -> setterTarget + "." + methodName + "(stream.getChar());";
                    case ReflectUtil.CLASS_INT -> setterTarget + "." + methodName + "(stream.getWInt());";
                    case ReflectUtil.CLASS_LONG -> setterTarget + "." + methodName + "(stream.getWLong());";
                    case ReflectUtil.CLASS_FLOAT -> setterTarget + "." + methodName + "(stream.getWFloat());";
                    case ReflectUtil.CLASS_DOUBLE -> setterTarget + "." + methodName + "(stream.getWDouble());";
                    case ReflectUtil.CLASS_BYTE -> setterTarget + "." + methodName + "(stream.getWByte());";
                    case ReflectUtil.CLASS_SHORT -> setterTarget + "." + methodName + "(stream.getWShort());";
                    case ReflectUtil.CLASS_STRING -> setterTarget + "." + methodName + "(stream.getStringValue());";
                    default -> "throw new IllegalArgumentException();";
                };
            }
            setContent.append(STR.format("""
                                                 case "{}"->{}
                                                 """, JsonRenameStrategy.helpGetRename(each), content));
        }
        setContent.append("}");
        builder.append(STR.format("""
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
                                                              {};
                                                          }
                                                          skipComma = stream.skipComma();
                                                      }
                                                      return instance;""", setContent.toString()));
        fromString.setBody(builder.toString());
        initMethod.setBody(initBody.toString());
        classModel.putMethodModel(fromString);
        classModel.putMethodModel(initMethod);
        return (TypeReader) Dson.DEFAULT_COMPILER_HELPER.compile(classModel).getConstructor().newInstance();
    }
}
