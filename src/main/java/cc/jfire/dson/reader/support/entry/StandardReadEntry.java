package cc.jfire.dson.reader.support.entry;

import cc.jfire.baseutil.reflect.ReflectUtil;
import cc.jfire.baseutil.reflect.valueaccessor.ValueAccessor;
import cc.jfire.dson.DsonConfig;
import cc.jfire.dson.DsonContext;
import cc.jfire.dson.reader.DeSerializeDefinition;
import cc.jfire.dson.reader.Stream;
import cc.jfire.dson.reader.TypeReader;
import cc.jfire.dson.reader.support.TypeResolver;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

public class StandardReadEntry implements ReadEntry
{
    protected       String                     name;
    protected       String                     fieldName;
    protected final TypeReader                 typeReader;
    protected final int                        classId;
    protected final ValueAccessor              valueAccessor;
    protected final Map<TypeVariable<?>, Type> typeVariableContext;

    public StandardReadEntry(String name, Field field, DsonContext dsonContext, Map<TypeVariable<?>, Type> typeVariableContext)
    {
        this.name      = name;
        this.fieldName = field.getName();
        DsonConfig config = dsonContext.getConfig();
        this.typeVariableContext = typeVariableContext;
        valueAccessor            = config.isValueAccessorUseCompile() ? ValueAccessor.compile(field) : ValueAccessor.standard(field);
        Type resolvedType = TypeResolver.resolveType(field.getGenericType(), typeVariableContext);
        if (resolvedType instanceof Class<?>)
        {
            classId = ReflectUtil.getClassId((Class) resolvedType);
        }
        else
        {
            classId = ReflectUtil.getClassId(field.getType());
        }
        if (field.isAnnotationPresent(DeSerializeDefinition.class))
        {
            DeSerializeDefinition annotation = field.getAnnotation(DeSerializeDefinition.class);
            try
            {
                typeReader = annotation.value().newInstance();
                typeReader.initialize(field.getGenericType(), dsonContext, typeVariableContext);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            if (ReflectUtil.isNonBoxedObject(classId) && classId != ReflectUtil.CLASS_STRING)
            {
                typeReader = dsonContext.parseReader(field.getGenericType(), typeVariableContext);
            }
            else
            {
                typeReader = null;
            }
        }
    }

    @Override
    public void setValue(Object instance, Stream stream)
    {
        if (typeReader != null)
        {
            switch (classId)
            {
                case ReflectUtil.PRIMITIVE_INT -> valueAccessor.set(instance, ((Number) typeReader.fromString(stream, typeVariableContext)).intValue());
                case ReflectUtil.PRIMITIVE_LONG -> valueAccessor.set(instance, ((Number) typeReader.fromString(stream, typeVariableContext)).longValue());
                case ReflectUtil.PRIMITIVE_FLOAT -> valueAccessor.set(instance, ((Number) typeReader.fromString(stream, typeVariableContext)).floatValue());
                case ReflectUtil.PRIMITIVE_DOUBLE -> valueAccessor.set(instance, ((Number) typeReader.fromString(stream, typeVariableContext)).doubleValue());
                case ReflectUtil.PRIMITIVE_BOOL -> valueAccessor.set(instance, ((Boolean) typeReader.fromString(stream, typeVariableContext)).booleanValue());
                case ReflectUtil.PRIMITIVE_BYTE -> valueAccessor.set(instance, ((Number) typeReader.fromString(stream, typeVariableContext)).byteValue());
                case ReflectUtil.PRIMITIVE_SHORT -> valueAccessor.set(instance, ((Number) typeReader.fromString(stream, typeVariableContext)).shortValue());
                case ReflectUtil.PRIMITIVE_CHAR -> valueAccessor.set(instance, ((Character) typeReader.fromString(stream, typeVariableContext)).charValue());
                default -> valueAccessor.setReference(instance, typeReader.fromString(stream, typeVariableContext));
            }
        }
        else
        {
            switch (classId)
            {
                case ReflectUtil.PRIMITIVE_INT -> valueAccessor.set(instance, stream.getInt());
                case ReflectUtil.PRIMITIVE_LONG -> valueAccessor.set(instance, stream.getLong());
                case ReflectUtil.PRIMITIVE_FLOAT -> valueAccessor.set(instance, stream.getFloat());
                case ReflectUtil.PRIMITIVE_DOUBLE -> valueAccessor.set(instance, stream.getDouble());
                case ReflectUtil.PRIMITIVE_BOOL -> valueAccessor.set(instance, stream.getBoolean());
                case ReflectUtil.PRIMITIVE_BYTE -> valueAccessor.set(instance, stream.getByte());
                case ReflectUtil.PRIMITIVE_SHORT -> valueAccessor.set(instance, stream.getShort());
                case ReflectUtil.PRIMITIVE_CHAR -> valueAccessor.set(instance, stream.getChar());
                case ReflectUtil.CLASS_INT -> valueAccessor.setReference(instance, stream.getWInt());
                case ReflectUtil.CLASS_LONG -> valueAccessor.setReference(instance, stream.getWLong());
                case ReflectUtil.CLASS_FLOAT -> valueAccessor.setReference(instance, stream.getWFloat());
                case ReflectUtil.CLASS_DOUBLE -> valueAccessor.setReference(instance, stream.getWDouble());
                case ReflectUtil.CLASS_BOOL -> valueAccessor.setReference(instance, stream.getBoolean());
                case ReflectUtil.CLASS_BYTE -> valueAccessor.setReference(instance, stream.getWByte());
                case ReflectUtil.CLASS_SHORT -> valueAccessor.setReference(instance, stream.getWShort());
                case ReflectUtil.CLASS_CHAR -> valueAccessor.setReference(instance, stream.getChar());
                case ReflectUtil.CLASS_STRING -> valueAccessor.setReference(instance, stream.getStringValue());
                default -> throw new IllegalArgumentException();
            }
        }
    }

    @Override
    public String name()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}



