package cc.jfire.dson.reader;

import cc.jfire.baseutil.STR;
import cc.jfire.baseutil.reflect.ReflectUtil;
import cc.jfire.baseutil.smc.SmcHelper;
import cc.jfire.baseutil.smc.model.ClassModel;
import cc.jfire.baseutil.smc.model.FieldModel;
import cc.jfire.baseutil.smc.model.MethodModel;
import cc.jfire.dson.Dson;
import cc.jfire.dson.reader.support.FieldIndexNode;
import cc.jfire.dson.util.JsonRenameStrategy;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicInteger;

public interface TypeReader
{
    default void initialize(Type type, ReaderContext readerContext)
    {
    }

    Object fromString(Stream stream);

    AtomicInteger COMPILE_COUNTER = new AtomicInteger();

    @SneakyThrows
    static TypeReader compile(Class ckazz)
    {
        ClassModel classModel = new ClassModel("BeanReader_" + COMPILE_COUNTER.getAndIncrement());
        classModel.addInterface(TypeReader.class);
        classModel.addImport(FieldIndexNode.class);
        classModel.addImport(ReaderContext.class);
        classModel.addImport(Stream.class);
        classModel.addField(new FieldModel("rootNode", FieldIndexNode.class, classModel));
        StringBuilder builder       = new StringBuilder();
        String        referenceName = SmcHelper.getReferenceName(ckazz, classModel);
        Field[]       fields        = ReflectUtil.findPojoBeanSetFields(ckazz);
        MethodModel   initMethod    = new MethodModel(TypeReader.class.getDeclaredMethod("initialize", Type.class, ReaderContext.class), classModel);
        initMethod.setParamterNames("type", "readerContext");
        StringBuilder initBody = new StringBuilder("""
                rootNode   = new FieldIndexNode();
                """);
        for (int i = 0; i < fields.length; i++)
        {
            initBody.append(STR.format("rootNode.put(\"{}\",{});\r\n", JsonRenameStrategy.helpGetRename(fields[i]), i));
        }
        MethodModel   fromString = new MethodModel(TypeReader.class.getDeclaredMethod("fromString", Stream.class), classModel);
        fromString.setParamterNames("stream");
        builder.append(referenceName).append(" instance = new ").append(referenceName).append("();\r\n");
        builder.append("stream.startParseObject();\r\n");
        builder.append("boolean skipComma = false;\r\n");
        StringBuilder setContent = new StringBuilder();
        setContent.append("switch(fieldIndex)\r\n{\r\n");
        classModel.addImport(DeSerializeDefinition.class);
        for (int i = 0; i < fields.length; i++)
        {
            Field each = fields[i];
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
                initBody.append(typeReaderName + ".initialize(readerContext.resolveType(type,field.getGenericType()),readerContext);\r\n");
                initBody.append("}\r\ncatch(Throwable e){cc.jfire.baseutil.reflect.ReflectUtil.throwException(e);}\r\n");
                content = STR.format("{}.{}(({}){}.fromString(stream));", setterTarget, methodName, SmcHelper.getReferenceName(each.getType(), classModel), typeReaderName);
            }
            else if (isInlineArray(each))
            {
                content = buildInlineArrayContent(each, setterTarget, methodName, classModel, initBody, declaringClassName);
            }
            else if (isInlineCollection(each))
            {
                content = buildInlineCollectionContent(each, setterTarget, methodName, classModel, initBody, declaringClassName);
            }
            else if (isInlineMap(each))
            {
                content = buildInlineMapContent(each, setterTarget, methodName, classModel, initBody, declaringClassName);
            }
            else if (ReflectUtil.isNonBoxedObject(each.getType()) && each.getType() != String.class)
            {
                String typeReaderName = "typeReader_" + COMPILE_COUNTER.getAndIncrement();
                classModel.addField(new FieldModel(typeReaderName, TypeReader.class, classModel));
                initBody.append("try\r\n{\r\n");
                initBody.append("java.lang.reflect.Field field = " + declaringClassName + ".class.getDeclaredField(\"" + each.getName() + "\");\r\n");
                initBody.append(typeReaderName + "=readerContext.parseReader(type,field.getGenericType());\r\n");
                initBody.append("}\r\ncatch(Throwable e){cc.jfire.baseutil.reflect.ReflectUtil.throwException(e);}\r\n");
                content = STR.format("{}.{}(({}){}.fromString(stream));", setterTarget, methodName, SmcHelper.getReferenceName(each.getType(), classModel), typeReaderName);
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
                                                 case {}->{}
                                                 """, i, content));
        }
        setContent.append("default -> stream.skipWholeValue();\r\n}");
        builder.append(STR.format("""
                                          while (skipComma || stream.parseObjectEnd() == false)
                                                      {
                                                          int fieldIndex = stream.getNameIndex(rootNode);
                                                          stream.skipColon();
                                                          if (fieldIndex == -1)
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

    private static boolean isInlineArray(Field field)
    {
        Class<?> arrayType = field.getType();
        if (arrayType.isArray() == false)
        {
            return false;
        }
        Class<?> componentType = arrayType.getComponentType();
        if (componentType.isArray())
        {
            return false;
        }
        return componentType == int.class
               || componentType == char.class
               || componentType == String.class
               || componentType == Integer.class
               || isPojoArrayComponent(componentType);
    }

    private static boolean isPojoArrayComponent(Class<?> componentType)
    {
        return componentType.isPrimitive() == false
               && ReflectUtil.isPrimitiveBox(componentType) == false
               && componentType != String.class
               && componentType != Object.class
               && componentType.isEnum() == false
               && java.util.Collection.class.isAssignableFrom(componentType) == false
               && java.util.Map.class.isAssignableFrom(componentType) == false;
    }

    private static String buildInlineArrayContent(Field field, String setterTarget, String methodName, ClassModel classModel, StringBuilder initBody, String declaringClassName)
    {
        Class<?> arrayType     = field.getType();
        Class<?> componentType = arrayType.getComponentType();
        String   arrayName     = SmcHelper.getReferenceName(arrayType, classModel);
        String   readElement;
        if (componentType == int.class)
        {
            readElement = "array[count] = stream.getInt();";
        }
        else if (componentType == char.class)
        {
            readElement = "array[count] = stream.getChar();";
        }
        else if (componentType == String.class)
        {
            readElement = "array[count] = stream.getStringValue();";
        }
        else if (componentType == Integer.class)
        {
            readElement = "array[count] = stream.getWInt();";
        }
        else
        {
            String componentName  = SmcHelper.getReferenceName(componentType, classModel);
            String typeReaderName = "typeReader_" + COMPILE_COUNTER.getAndIncrement();
            classModel.addField(new FieldModel(typeReaderName, TypeReader.class, classModel));
            initBody.append("try\r\n{\r\n");
            initBody.append("java.lang.reflect.Field field = " + declaringClassName + ".class.getDeclaredField(\"" + field.getName() + "\");\r\n");
            initBody.append(typeReaderName + "=readerContext.parseReader(field.getType().getComponentType());\r\n");
            initBody.append("}\r\ncatch(Throwable e){cc.jfire.baseutil.reflect.ReflectUtil.throwException(e);}\r\n");
            readElement = STR.format("array[count] = ({}){}.fromString(stream);", componentName, typeReaderName);
        }
        return STR.format("""
                {
                    stream.startParseArray();
                    int count = 0;
                    {} array = new {}[16];
                    while (stream.parseArrayEnd() == false)
                    {
                        if (count == array.length)
                        {
                            array = java.util.Arrays.copyOf(array, array.length * 2);
                        }
                        if (stream.isNextNullAndSkip())
                        {
                            ;
                        }
                        else
                        {
                            {}
                            count += 1;
                        }
                        stream.skipComma();
                    }
                    {}.{}(java.util.Arrays.copyOf(array, count));
                }
                """, arrayName, SmcHelper.getReferenceName(componentType, classModel), readElement, setterTarget, methodName);
    }

    private static boolean isInlineCollection(Field field)
    {
        Class<?> fieldType = field.getType();
        if (fieldType != java.util.ArrayList.class && fieldType != java.util.List.class)
        {
            return false;
        }
        if (field.getGenericType() instanceof ParameterizedType parameterizedType)
        {
            Type elementType = parameterizedType.getActualTypeArguments()[0];
            if (elementType instanceof Class<?> elementClass)
            {
                return isInlineCollectionElement(elementClass);
            }
        }
        return false;
    }

    private static boolean isInlineCollectionElement(Class<?> elementClass)
    {
        if (elementClass.isArray())
        {
            return false;
        }
        return elementClass == String.class
               || ReflectUtil.isPrimitiveBox(elementClass)
               || isPojoArrayComponent(elementClass);
    }

    private static String buildInlineCollectionContent(Field field, String setterTarget, String methodName, ClassModel classModel, StringBuilder initBody, String declaringClassName)
    {
        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
        Class<?>          elementClass      = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        String            readElement       = buildInlineCollectionReadElement(field, elementClass, classModel, initBody, declaringClassName);
        return STR.format("""
                {
                    java.util.ArrayList collection = new java.util.ArrayList();
                    stream.startParseArray();
                    while (stream.parseArrayEnd() == false)
                    {
                        if (stream.isNextNullAndSkip())
                        {
                            ;
                        }
                        else
                        {
                            collection.add({});
                        }
                        stream.skipComma();
                    }
                    {}.{}(collection);
                }
                """, readElement, setterTarget, methodName);
    }

    private static String buildInlineCollectionReadElement(Field field, Class<?> elementClass, ClassModel classModel, StringBuilder initBody, String declaringClassName)
    {
        int classId = ReflectUtil.getClassId(elementClass);
        return switch (classId)
        {
            case ReflectUtil.CLASS_BOOL -> "stream.getBoolean()";
            case ReflectUtil.CLASS_BYTE -> "stream.getWByte()";
            case ReflectUtil.CLASS_SHORT -> "stream.getWShort()";
            case ReflectUtil.CLASS_INT -> "stream.getWInt()";
            case ReflectUtil.CLASS_LONG -> "stream.getWLong()";
            case ReflectUtil.CLASS_FLOAT -> "stream.getWFloat()";
            case ReflectUtil.CLASS_DOUBLE -> "stream.getWDouble()";
            case ReflectUtil.CLASS_CHAR -> "stream.getChar()";
            case ReflectUtil.CLASS_STRING -> "stream.getStringValue()";
            default ->
            {
                String typeReaderName = "typeReader_" + COMPILE_COUNTER.getAndIncrement();
                classModel.addField(new FieldModel(typeReaderName, TypeReader.class, classModel));
                initBody.append("try\r\n{\r\n");
                initBody.append("java.lang.reflect.Field field = " + declaringClassName + ".class.getDeclaredField(\"" + field.getName() + "\");\r\n");
                initBody.append("java.lang.reflect.ParameterizedType parameterizedType = (java.lang.reflect.ParameterizedType) field.getGenericType();\r\n");
                initBody.append(typeReaderName + "=readerContext.parseReader(parameterizedType.getActualTypeArguments()[0]);\r\n");
                initBody.append("}\r\ncatch(Throwable e){cc.jfire.baseutil.reflect.ReflectUtil.throwException(e);}\r\n");
                yield typeReaderName + ".fromString(stream)";
            }
        };
    }

    private static boolean isInlineMap(Field field)
    {
        Class<?> fieldType = field.getType();
        if (fieldType != java.util.HashMap.class && fieldType != java.util.Map.class)
        {
            return false;
        }
        if (field.getGenericType() instanceof ParameterizedType parameterizedType)
        {
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (actualTypeArguments[0] == String.class && actualTypeArguments[1] instanceof Class<?> valueClass)
            {
                return isInlineMapValue(valueClass);
            }
        }
        return false;
    }

    private static boolean isInlineMapValue(Class<?> valueClass)
    {
        if (valueClass.isArray())
        {
            return false;
        }
        return valueClass == String.class
               || ReflectUtil.isPrimitiveBox(valueClass)
               || isPojoArrayComponent(valueClass);
    }

    private static String buildInlineMapContent(Field field, String setterTarget, String methodName, ClassModel classModel, StringBuilder initBody, String declaringClassName)
    {
        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
        Class<?>          valueClass        = (Class<?>) parameterizedType.getActualTypeArguments()[1];
        String            readValue         = buildInlineMapReadValue(field, valueClass, classModel, initBody, declaringClassName);
        return STR.format("""
                {
                    java.util.HashMap map = new java.util.HashMap();
                    stream.startParseObject();
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
                            map.put(name, {});
                        }
                        stream.skipComma();
                    }
                    {}.{}(map);
                }
                """, readValue, setterTarget, methodName);
    }

    private static String buildInlineMapReadValue(Field field, Class<?> valueClass, ClassModel classModel, StringBuilder initBody, String declaringClassName)
    {
        int classId = ReflectUtil.getClassId(valueClass);
        return switch (classId)
        {
            case ReflectUtil.CLASS_BOOL -> "stream.getBoolean()";
            case ReflectUtil.CLASS_BYTE -> "stream.getWByte()";
            case ReflectUtil.CLASS_SHORT -> "stream.getWShort()";
            case ReflectUtil.CLASS_INT -> "stream.getWInt()";
            case ReflectUtil.CLASS_LONG -> "stream.getWLong()";
            case ReflectUtil.CLASS_FLOAT -> "stream.getWFloat()";
            case ReflectUtil.CLASS_DOUBLE -> "stream.getWDouble()";
            case ReflectUtil.CLASS_CHAR -> "stream.getChar()";
            case ReflectUtil.CLASS_STRING -> "stream.getStringValue()";
            default ->
            {
                String typeReaderName = "typeReader_" + COMPILE_COUNTER.getAndIncrement();
                classModel.addField(new FieldModel(typeReaderName, TypeReader.class, classModel));
                initBody.append("try\r\n{\r\n");
                initBody.append("java.lang.reflect.Field field = " + declaringClassName + ".class.getDeclaredField(\"" + field.getName() + "\");\r\n");
                initBody.append("java.lang.reflect.ParameterizedType parameterizedType = (java.lang.reflect.ParameterizedType) field.getGenericType();\r\n");
                initBody.append(typeReaderName + "=readerContext.parseReader(parameterizedType.getActualTypeArguments()[1]);\r\n");
                initBody.append("}\r\ncatch(Throwable e){cc.jfire.baseutil.reflect.ReflectUtil.throwException(e);}\r\n");
                yield typeReaderName + ".fromString(stream)";
            }
        };
    }
}
