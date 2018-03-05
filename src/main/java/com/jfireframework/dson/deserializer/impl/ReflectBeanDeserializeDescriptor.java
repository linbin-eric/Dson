package com.jfireframework.dson.deserializer.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.dson.deserializer.DeserializeDescriptor;
import com.jfireframework.dson.deserializer.Deserializer;
import com.jfireframework.dson.metadata.json.DsonObject;
import com.jfireframework.dson.metadata.json.Element;
import com.jfireframework.dson.metadata.json.Entry;
import com.jfireframework.dson.metadata.json.JsonCollection;
import com.jfireframework.dson.metadata.json.JsonValueType;
import com.jfireframework.dson.metadata.parse.Lexer;

public class ReflectBeanDeserializeDescriptor implements DeserializeDescriptor
{
    interface PropertyDeserializeDescriptor
    {
        void deserialize(Object bean, Entry entry);
    }
    
    private Map<String, PropertyDeserializeDescriptor> store = new HashMap<String, PropertyDeserializeDescriptor>();
    private Class<?>                                   type;
    private Deserializer                               deserializer;
    
    @Override
    public void initialize(Type type, Deserializer deserializer, Map<Type, DeserializeDescriptor> map)
    {
        this.deserializer = deserializer;
        this.type = (Class<?>) type;
        for (Field field : ReflectUtil.getAllFields(this.type))
        {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers()))
            {
                continue;
            }
            Class<?> fieldType = field.getType();
            BasePropertyDeserializeDescriber propertyDeserializeDescriptor = null;
            if (fieldType == int.class || fieldType == Integer.class)
            {
                propertyDeserializeDescriptor = new IntegerPropertyDescriptor();
            }
            else if (fieldType == byte.class || fieldType == Byte.class)
            {
                propertyDeserializeDescriptor = new BytePropertyDescriptor();
            }
            else if (fieldType == long.class || fieldType == Long.class)
            {
                propertyDeserializeDescriptor = new LongPropertyDescriptor();
            }
            else if (fieldType == short.class || fieldType == Short.class)
            {
                propertyDeserializeDescriptor = new ShortPropertyDescriber();
            }
            else if (fieldType == float.class || fieldType == Float.class)
            {
                propertyDeserializeDescriptor = new FloatPropertyDescriptor();
            }
            else if (fieldType == double.class || fieldType == Double.class)
            {
                propertyDeserializeDescriptor = new DoublePropertyDescriptor();
            }
            else if (fieldType == boolean.class || fieldType == Boolean.class)
            {
                propertyDeserializeDescriptor = new BooleanPropertyDescriptor();
            }
            else if (fieldType == char.class || fieldType == Character.class)
            {
                propertyDeserializeDescriptor = new CharacterPropertyDescriptor();
            }
            else if (fieldType == String.class)
            {
                propertyDeserializeDescriptor = new StringPropertyDescriptor();
            }
            else if (Enum.class.isAssignableFrom(fieldType))
            {
                propertyDeserializeDescriptor = new EnumPropertyDescriptor();
            }
            else if (Map.class.isAssignableFrom(fieldType))
            {
                propertyDeserializeDescriptor = new BeanPropertyDescriptor();
            }
            else if (Collection.class.isAssignableFrom(fieldType))
            {
                propertyDeserializeDescriptor = new CollectionPropertyDescriptor();
            }
            else if (fieldType.isArray())
            {
                propertyDeserializeDescriptor = new CollectionPropertyDescriptor();
            }
            else if (fieldType == Object.class)
            {
                propertyDeserializeDescriptor = new ObjectPropertyDescriptor();
            }
            else
            {
                propertyDeserializeDescriptor = new BeanPropertyDescriptor();
            }
            propertyDeserializeDescriptor.initialize(field, map);
            store.put(field.getName(), propertyDeserializeDescriptor);
        }
    }
    
    @Override
    public Object deserialize(DsonObject dsonObject)
    {
        JsonCollection collection = (JsonCollection) dsonObject;
        return deserialize(collection);
    }
    
    private Object deserialize(JsonCollection collection)
    {
        try
        {
            Object instance = type.newInstance();
            for (Entry entry : collection.getEntries())
            {
                PropertyDeserializeDescriptor propertyDeserializeDescriber = store.get(entry.getName());
                if (propertyDeserializeDescriber != null)
                {
                    propertyDeserializeDescriber.deserialize(instance, entry);
                }
            }
            return instance;
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
    }
    
    @Override
    public Object deserialize(String json)
    {
        return deserialize((JsonCollection) new Lexer(json).parse());
    }
    
    @Override
    public Object deserialize(Entry entry)
    {
        if (entry.getValueType() != JsonValueType.COLLECTION)
        {
            throw new IllegalArgumentException();
        }
        return deserialize((JsonCollection) entry.getValue());
    }
    
    @Override
    public Object deserialize(Element element)
    {
        if (element.getValueType() != JsonValueType.COLLECTION)
        {
            throw new IllegalArgumentException();
        }
        return deserialize((JsonCollection) element.getValue());
    }
    
    abstract class BasePropertyDeserializeDescriber implements PropertyDeserializeDescriptor
    {
        protected Field field;
        
        protected void initialize(Field field, Map<Type, DeserializeDescriptor> map)
        {
            this.field = field;
            field.setAccessible(true);
        }
        
        protected void setValue(Object bean, Object value)
        {
            try
            {
                field.set(bean, value);
            }
            catch (Exception e)
            {
                throw new JustThrowException(e);
            }
        }
    }
    
    class BytePropertyDescriptor extends BasePropertyDeserializeDescriber
    {
        
        @Override
        public void deserialize(Object bean, Entry entry)
        {
            if (entry.getValueType() != JsonValueType.NUMBER_LONG)
            {
                return;
            }
            setValue(bean, ((Long) entry.getValue()).byteValue());
        }
    }
    
    class IntegerPropertyDescriptor extends BasePropertyDeserializeDescriber
    {
        
        @Override
        public void deserialize(Object bean, Entry entry)
        {
            if (entry.getValueType() != JsonValueType.NUMBER_LONG)
            {
                return;
            }
            setValue(bean, ((Long) entry.getValue()).intValue());
        }
    }
    
    class LongPropertyDescriptor extends BasePropertyDeserializeDescriber
    {
        
        @Override
        public void deserialize(Object bean, Entry entry)
        {
            if (entry.getValueType() != JsonValueType.NUMBER_LONG)
            {
                return;
            }
            setValue(bean, (entry.getValue()));
        }
    }
    
    class ShortPropertyDescriber extends BasePropertyDeserializeDescriber
    {
        
        @Override
        public void deserialize(Object bean, Entry entry)
        {
            if (entry.getValueType() != JsonValueType.NUMBER_LONG)
            {
                return;
            }
            setValue(bean, ((Long) entry.getValue()).shortValue());
        }
    }
    
    class FloatPropertyDescriptor extends BasePropertyDeserializeDescriber
    {
        
        @Override
        public void deserialize(Object bean, Entry entry)
        {
            if (entry.getValueType() != JsonValueType.NUMBER_DOUBLE)
            {
                return;
            }
            setValue(bean, ((Double) entry.getValue()).floatValue());
        }
    }
    
    class DoublePropertyDescriptor extends BasePropertyDeserializeDescriber
    {
        
        @Override
        public void deserialize(Object bean, Entry entry)
        {
            if (entry.getValueType() != JsonValueType.NUMBER_DOUBLE)
            {
                return;
            }
            setValue(bean, (entry.getValue()));
        }
    }
    
    class BooleanPropertyDescriptor extends BasePropertyDeserializeDescriber
    {
        
        @Override
        public void deserialize(Object bean, Entry entry)
        {
            if (entry.getValueType() != JsonValueType.BOOLEAN)
            {
                return;
            }
            setValue(bean, (entry.getValue()));
        }
    }
    
    class StringPropertyDescriptor extends BasePropertyDeserializeDescriber
    {
        
        @Override
        public void deserialize(Object bean, Entry entry)
        {
            if (entry.getValueType() != JsonValueType.STRING)
            {
                return;
            }
            setValue(bean, (entry.getValue()));
        }
    }
    
    class CharacterPropertyDescriptor extends BasePropertyDeserializeDescriber
    {
        
        @Override
        public void deserialize(Object bean, Entry entry)
        {
            if (entry.getValueType() != JsonValueType.STRING)
            {
                return;
            }
            setValue(bean, ((String) entry.getValue()).charAt(0));
        }
    }
    
    class EnumPropertyDescriptor extends BasePropertyDeserializeDescriber
    {
        Map<String, ? extends Enum<?>> allEnumInstances;
        
        @SuppressWarnings("unchecked")
        @Override
        protected void initialize(Field field, Map<Type, DeserializeDescriptor> map)
        {
            super.initialize(field, map);
            allEnumInstances = ReflectUtil.getAllEnumInstances((Class<? extends Enum<?>>) field.getType());
        }
        
        @Override
        public void deserialize(Object bean, Entry entry)
        {
            if (entry.getValueType() != JsonValueType.STRING)
            {
                return;
            }
            setValue(bean, allEnumInstances.get(entry.getValue()));
        }
        
    }
    
    class ObjectPropertyDescriptor extends BasePropertyDeserializeDescriber
    {
        
        @Override
        public void deserialize(Object bean, Entry entry)
        {
            setValue(bean, entry.getValue());
        }
        
    }
    
    class BeanPropertyDescriptor extends BasePropertyDeserializeDescriber
    {
        private DeserializeDescriptor deserializeDescriber;
        
        @Override
        protected void initialize(Field field, Map<Type, DeserializeDescriptor> map)
        {
            super.initialize(field, map);
            deserializeDescriber = deserializer.describe(field.getGenericType(), map);
        }
        
        @Override
        public void deserialize(Object bean, Entry entry)
        {
            if (entry.getValueType() != JsonValueType.COLLECTION)
            {
                return;
            }
            setValue(bean, deserializeDescriber.deserialize(entry));
        }
    }
    
    class CollectionPropertyDescriptor extends BasePropertyDeserializeDescriber
    {
        private DeserializeDescriptor deserializeDescriber;
        
        @Override
        protected void initialize(Field field, Map<Type, DeserializeDescriptor> map)
        {
            super.initialize(field, map);
            deserializeDescriber = deserializer.describe(field.getGenericType(), map);
        }
        
        @Override
        public void deserialize(Object bean, Entry entry)
        {
            if (entry.getValueType() != JsonValueType.ARRAY)
            {
                return;
            }
            setValue(bean, deserializeDescriber.deserialize(entry));
        }
        
    }
}
