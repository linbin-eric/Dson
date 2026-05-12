package cc.jfire.dson.writer.impl;

import cc.jfire.baseutil.reflect.ReflectUtil;
import cc.jfire.baseutil.reflect.valueaccessor.ValueAccessor;
import cc.jfire.dson.DsonContext;
import cc.jfire.dson.dynamic.DynamicJsonValue;
import cc.jfire.dson.dynamic.DynamicModifyValue;
import cc.jfire.dson.util.JsonIgnore;
import cc.jfire.dson.util.JsonRenameStrategy;
import cc.jfire.dson.util.WriterUtil;
import cc.jfire.dson.writer.SerializeDefinition;
import cc.jfire.dson.writer.TypeWriter;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ObjectWriter implements TypeWriter
{
    private Entry[] entries;
    private boolean dynamicJsonValue = false;
    private boolean dynamicModify    = false;

    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        if (entries.length == 0)
        {
            return;
        }
        if (dynamicModify)
        {
        }
        else
        {
            output.append('{');
            int length = output.length();
            for (Entry each : entries)
            {
                each.output(output, entity);
            }
            int newLength = output.length();
            if (length != newLength)
            {
                output.setLength(newLength - 1);
            }
            output.append('}');
        }
    }

    @Override
    public Object toJsonValue(Object entity)
    {
        if (dynamicJsonValue)
        {
            return ((DynamicJsonValue) entity).toJsonValue();
        }
        else
        {
            Map<String, Object> map = new HashMap<>();
            for (Entry each : entries)
            {
                each.toJsonValue(map, entity);
            }
            return map;
        }
    }

    @SneakyThrows
    @Override
    public void initialize(Type type, DsonContext dsonContext)
    {
        Class ckazz = (Class) type;
        dynamicJsonValue = DynamicJsonValue.class.isAssignableFrom(ckazz);
        dynamicModify    = DynamicModifyValue.class.isAssignableFrom(ckazz);
        Map<String, Field> map = new HashMap<>();
        while (ckazz != Object.class)
        {
            for (Field each : ckazz.getDeclaredFields())
            {
                if (Modifier.isStatic(each.getModifiers()) || (each.isAnnotationPresent(JsonIgnore.class) && each.getAnnotation(JsonIgnore.class).ignoreWrite()))
                {
                    continue;
                }
                map.putIfAbsent(JsonRenameStrategy.helpGetRename(each), each);
            }
            ckazz = ckazz.getSuperclass();
        }
        List<Entry> list = new LinkedList<>();
        for (Map.Entry<String, Field> each : map.entrySet())
        {
            int   classId = ReflectUtil.getClassId(each.getValue().getType());
            Entry entry   = null;
            if (each.getValue().isAnnotationPresent(SerializeDefinition.class))
            {
                TypeWriter typeWriter = each.getValue().getAnnotation(SerializeDefinition.class).value().getConstructor().newInstance();
                typeWriter.initialize(each.getValue().getGenericType(), dsonContext);
                entry = new PreDedfinedTypeEntry().setTypeWriter(typeWriter);
            }
            else
            {
                switch (classId)
                {
                    case ReflectUtil.PRIMITIVE_INT -> entry = new IntEntry();
                    case ReflectUtil.PRIMITIVE_BYTE -> entry = new ByteEntry();
                    case ReflectUtil.PRIMITIVE_CHAR -> entry = new CharEntry();
                    case ReflectUtil.PRIMITIVE_BOOL -> entry = new BoolEntry();
                    case ReflectUtil.PRIMITIVE_SHORT -> entry = new ShortEntry();
                    case ReflectUtil.PRIMITIVE_LONG -> entry = new LongEntry();
                    case ReflectUtil.PRIMITIVE_FLOAT -> entry = new FloatEntry();
                    case ReflectUtil.PRIMITIVE_DOUBLE -> entry = new DoubleEntry();
                    case ReflectUtil.CLASS_INT, ReflectUtil.CLASS_BYTE, ReflectUtil.CLASS_BOOL, ReflectUtil.CLASS_SHORT, ReflectUtil.CLASS_LONG, ReflectUtil.CLASS_FLOAT,
                         ReflectUtil.CLASS_DOUBLE -> entry = new BoxedNonCharacterEntry();
                    case ReflectUtil.CLASS_CHAR -> entry = new CharacterEntry();
                    case ReflectUtil.CLASS_STRING -> entry = new StringEntry();
                    default ->
                    {
                        if (Modifier.isFinal(each.getValue().getType().getModifiers()))
                        {
                            TypeWriter typeWriter = dsonContext.parseWriter(each.getValue().getGenericType());
                            entry = new PreDedfinedTypeEntry().setTypeWriter(typeWriter);
                        }
                        else
                        {
                            entry = new UnPreDefinedTypeEntry().setDsonContext(dsonContext);
                        }
                    }
                }
            }
            entry.setFullName('"' + each.getKey() + '"' + ':')//
                 .setName(each.getKey())//
                 .setValueAccessor(dsonContext.getConfig().isValueAccessorUseCompile() ? ValueAccessor.compile(each.getValue()) : ValueAccessor.standard(each.getValue()));
            list.add(entry);
        }
        this.entries = list.toArray(Entry[]::new);
    }

    @Data
    @Accessors(chain = true)
    abstract class Entry
    {
        protected ValueAccessor valueAccessor;
        protected TypeWriter    typeWriter;
        protected String        fullName;
        protected String        name;

        public abstract void output(StringBuilder builder, Object instance);

        public abstract void toJsonValue(Map<String, Object> map, Object instance);
    }

    class IntEntry extends Entry
    {
        @Override
        public void output(StringBuilder builder, Object instance)
        {
            builder.append(fullName);
            builder.append(valueAccessor.getInt(instance));
            builder.append(',');
        }

        @Override
        public void toJsonValue(Map<String, Object> map, Object instance)
        {
            map.put(name, valueAccessor.getInt(instance));
        }
    }

    class ByteEntry extends Entry
    {
        @Override
        public void output(StringBuilder builder, Object instance)
        {
            builder.append(fullName);
            builder.append(valueAccessor.getByte(instance));
            builder.append(',');
        }

        @Override
        public void toJsonValue(Map<String, Object> map, Object instance)
        {
            map.put(name, valueAccessor.getByte(instance));
        }
    }

    class CharEntry extends Entry
    {
        @Override
        public void output(StringBuilder builder, Object instance)
        {
            builder.append(fullName);
            builder.append('"').append(valueAccessor.getChar(instance));
            builder.append('"').append(',');
        }

        @Override
        public void toJsonValue(Map<String, Object> map, Object instance)
        {
            map.put(name, valueAccessor.getChar(instance));
        }
    }

    class BoolEntry extends Entry
    {
        @Override
        public void output(StringBuilder builder, Object instance)
        {
            builder.append(fullName);
            builder.append(valueAccessor.getBoolean(instance));
            builder.append(',');
        }

        @Override
        public void toJsonValue(Map<String, Object> map, Object instance)
        {
            map.put(name, valueAccessor.getBoolean(instance));
        }
    }

    class ShortEntry extends Entry
    {
        @Override
        public void output(StringBuilder builder, Object instance)
        {
            builder.append(fullName);
            builder.append(valueAccessor.getShort(instance));
            builder.append(',');
        }

        @Override
        public void toJsonValue(Map<String, Object> map, Object instance)
        {
            map.put(name, valueAccessor.getShort(instance));
        }
    }

    class LongEntry extends Entry
    {
        @Override
        public void output(StringBuilder builder, Object instance)
        {
            builder.append(fullName);
            builder.append(valueAccessor.getLong(instance));
            builder.append(',');
        }

        @Override
        public void toJsonValue(Map<String, Object> map, Object instance)
        {
            map.put(name, valueAccessor.getLong(instance));
        }
    }

    class FloatEntry extends Entry
    {
        @Override
        public void output(StringBuilder builder, Object instance)
        {
            builder.append(fullName);
            builder.append(valueAccessor.getFloat(instance));
            builder.append(',');
        }

        @Override
        public void toJsonValue(Map<String, Object> map, Object instance)
        {
            map.put(name, valueAccessor.getFloat(instance));
        }
    }

    class DoubleEntry extends Entry
    {
        @Override
        public void output(StringBuilder builder, Object instance)
        {
            builder.append(fullName);
            builder.append(valueAccessor.getDouble(instance));
            builder.append(',');
        }

        @Override
        public void toJsonValue(Map<String, Object> map, Object instance)
        {
            map.put(name, valueAccessor.getDouble(instance));
        }
    }

    class StringEntry extends Entry
    {
        @Override
        public void output(StringBuilder builder, Object instance)
        {
            String reference = (String) valueAccessor.getReference(instance);
            if (reference != null)
            {
                builder.append(fullName);
                builder.append('"');
                WriterUtil.writeString(builder, reference);
                builder.append('"').append(',');
            }
        }

        @Override
        public void toJsonValue(Map<String, Object> map, Object instance)
        {
            String reference = (String) valueAccessor.getReference(instance);
            if (reference != null)
            {
                map.put(name, reference);
            }
        }
    }

    class BoxedNonCharacterEntry extends Entry
    {
        @Override
        public void output(StringBuilder builder, Object instance)
        {
            Object reference = valueAccessor.getReference(instance);
            if (reference != null)
            {
                builder.append(fullName);
                builder.append(reference);
                builder.append(',');
            }
        }

        @Override
        public void toJsonValue(Map<String, Object> map, Object instance)
        {
            Object reference = valueAccessor.getReference(instance);
            if (reference != null)
            {
                map.put(name, reference);
            }
        }
    }

    class CharacterEntry extends Entry
    {
        @Override
        public void output(StringBuilder builder, Object instance)
        {
            Character reference = (Character) valueAccessor.getReference(instance);
            if (reference != null)
            {
                builder.append(fullName);
                builder.append('"').append(reference);
                builder.append('"').append(',');
            }
        }

        @Override
        public void toJsonValue(Map<String, Object> map, Object instance)
        {
            Character reference = (Character) valueAccessor.getReference(instance);
            if (reference != null)
            {
                map.put(name, reference);
            }
        }
    }

    class PreDedfinedTypeEntry extends Entry
    {
        @Override
        public void output(StringBuilder builder, Object instance)
        {
            Object reference = valueAccessor.getReference(instance);
            if (reference != null)
            {
                builder.append(fullName);
                typeWriter.toJson(reference, builder);
                builder.append(',');
            }
        }

        @Override
        public void toJsonValue(Map<String, Object> map, Object instance)
        {
            Object reference = valueAccessor.getReference(instance);
            if (reference != null)
            {
                map.put(name, typeWriter.toJsonValue(reference));
            }
        }
    }

    @Data
    @Accessors(chain = true)
    class UnPreDefinedTypeEntry extends Entry
    {
        private DsonContext dsonContext;

        @Override
        public void output(StringBuilder builder, Object instance)
        {
            Object reference = valueAccessor.getReference(instance);
            if (reference != null)
            {
                builder.append(fullName);
                dsonContext.parseWriter(reference.getClass()).toJson(reference, builder);
                builder.append(',');
            }
        }

        @Override
        public void toJsonValue(Map<String, Object> map, Object instance)
        {
            Object reference = valueAccessor.getReference(instance);
            if (reference != null)
            {
                map.put(name, dsonContext.parseWriter(reference.getClass()).toJsonValue(reference));
            }
        }
    }
}
