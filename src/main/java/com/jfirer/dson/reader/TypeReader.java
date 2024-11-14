package com.jfirer.dson.reader;

import com.jfirer.baseutil.STR;
import com.jfirer.baseutil.reflect.ReflectUtil;
import com.jfirer.baseutil.smc.SmcHelper;
import com.jfirer.baseutil.smc.compiler.CompileHelper;
import com.jfirer.baseutil.smc.model.ClassModel;
import com.jfirer.baseutil.smc.model.FieldModel;
import com.jfirer.baseutil.smc.model.MethodModel;
import com.jfirer.dson.DsonContext;
import com.jfirer.dson.reader.support.Node;
import com.jfirer.dson.reader.support.entry.ReadEntry;
import com.jfirer.dson.util.InitializeStatusHolder;
import com.jfirer.dson.util.JsonRenameStrategy;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public interface TypeReader extends InitializeStatusHolder
{
    default void initialize(Type type, DsonContext dsonContext)
    {
    }

    Object fromString(Stream stream);

    @SneakyThrows
    static TypeReader compile(Class ckazz)
    {
        ClassModel classModel = new ClassModel("BeanReader_" + CompileHelper.COMPILE_COUNTER.getAndIncrement(), InitializeStatusHolderImpl.class);
        classModel.addInterface(TypeReader.class);
        classModel.addImport(Node.class);
        classModel.addImport(Stream.class);
        classModel.addImport(ReadEntry.class);
        classModel.addField(new FieldModel("rootNode", Node.class, classModel));
        MethodModel initMethod = new MethodModel(TypeReader.class.getDeclaredMethod("initialize", Type.class, DsonContext.class), classModel);
        initMethod.setParamterNames("ckass", "dsonContext");
        StringBuilder initBody   = new StringBuilder("rootNode   = Node.generateRoot((Class)ckass, dsonContext);\r\n");
        MethodModel   fromString = new MethodModel(TypeReader.class.getDeclaredMethod("fromString", Stream.class), classModel);
        fromString.setParamterNames("stream");
        StringBuilder builder       = new StringBuilder();
        String        referenceName = SmcHelper.getReferenceName(ckazz, classModel);
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
            if (each.isAnnotationPresent(DeSerializeDefinition.class))
            {
                String typeReaderName = "typeReader_" + CompileHelper.COMPILE_COUNTER.getAndIncrement();
                classModel.addField(new FieldModel(typeReaderName, TypeReader.class, classModel));
                initBody.append("try\r\n{\r\n");
                initBody.append(typeReaderName + "=" + SmcHelper.getReferenceName(each.getDeclaringClass(), classModel) + ".class.getDeclaredField(\"" + each.getName() + "\").getAnnotation(DeSerializeDefinition.class).value().newInstance();\r\n");
                initBody.append(typeReaderName + ".initialize(ckass,dsonContext);\r\n");
                initBody.append("}\r\ncatch(Throwable e){;}\r\n");
                content = STR.format("instance.{}(({}){}.fromString(stream));", ReflectUtil.parseBeanSetMethodName(each), SmcHelper.getReferenceName(each.getType(), classModel), typeReaderName);
            }
            else if (ReflectUtil.isNonBoxedObject(each.getType()) && each.getType() != String.class)
            {
                String typeReaderName = "typeReader_" + CompileHelper.COMPILE_COUNTER.getAndIncrement();
                classModel.addField(new FieldModel(typeReaderName, TypeReader.class, classModel));
                initBody.append("try\r\n{\r\n");
                initBody.append(typeReaderName + "=dsonContext.parseReader(" + SmcHelper.getReferenceName(each.getDeclaringClass(), classModel) + ".class.getDeclaredField(\"" + each.getName() + "\").getGenericType());");
                initBody.append("}\r\ncatch(Throwable e){;}\r\n");
                content = STR.format("instance.{}(({}){}.fromString(stream));", ReflectUtil.parseBeanSetMethodName(each), SmcHelper.getReferenceName(each.getType(), classModel), typeReaderName);
            }
            else
            {
                int classId = ReflectUtil.getClassId(each.getType());
                content = switch (classId)
                {
                    case ReflectUtil.PRIMITIVE_INT -> "instance." + ReflectUtil.parseBeanSetMethodName(each) + "(stream.getInt());";
                    case ReflectUtil.PRIMITIVE_LONG -> "instance." + ReflectUtil.parseBeanSetMethodName(each) + "(stream.getLong());";
                    case ReflectUtil.PRIMITIVE_FLOAT -> "instance." + ReflectUtil.parseBeanSetMethodName(each) + "(stream.getFloat());";
                    case ReflectUtil.PRIMITIVE_DOUBLE -> "instance." + ReflectUtil.parseBeanSetMethodName(each) + "(stream.getDouble());";
                    case ReflectUtil.PRIMITIVE_BOOL, ReflectUtil.CLASS_BOOL -> "instance." + ReflectUtil.parseBeanSetMethodName(each) + "(stream.getBoolean());";
                    case ReflectUtil.PRIMITIVE_BYTE -> "instance." + ReflectUtil.parseBeanSetMethodName(each) + "(stream.getByte());";
                    case ReflectUtil.PRIMITIVE_SHORT -> "instance." + ReflectUtil.parseBeanSetMethodName(each) + "(stream.getShort());";
                    case ReflectUtil.PRIMITIVE_CHAR, ReflectUtil.CLASS_CHAR -> "instance." + ReflectUtil.parseBeanSetMethodName(each) + "(stream.getChar());";
                    case ReflectUtil.CLASS_INT -> "instance." + ReflectUtil.parseBeanSetMethodName(each) + "(stream.getWInt());";
                    case ReflectUtil.CLASS_LONG -> "instance." + ReflectUtil.parseBeanSetMethodName(each) + "(stream.getWLong());";
                    case ReflectUtil.CLASS_FLOAT -> "instance." + ReflectUtil.parseBeanSetMethodName(each) + "(stream.getWFloat());";
                    case ReflectUtil.CLASS_DOUBLE -> "instance." + ReflectUtil.parseBeanSetMethodName(each) + "(stream.getWDouble());";
                    case ReflectUtil.CLASS_BYTE -> "instance." + ReflectUtil.parseBeanSetMethodName(each) + "(stream.getWByte());";
                    case ReflectUtil.CLASS_SHORT -> "instance." + ReflectUtil.parseBeanSetMethodName(each) + "(stream.getWShort());";
                    case ReflectUtil.CLASS_STRING -> "instance." + ReflectUtil.parseBeanSetMethodName(each) + "(stream.getStringValue());";
                    default -> "instance." + ReflectUtil.parseBeanSetMethodName(each) + "(typeReader.fromString(stream));";
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
        initBody.append("setInitialized();\r\n");
        initMethod.setBody(initBody.toString());
        classModel.putMethodModel(fromString);
        classModel.putMethodModel(initMethod);
        return (TypeReader) CompileHelper.DEFAULT_COMPILE_HELPER.compile(classModel).getConstructor().newInstance();
    }
}
