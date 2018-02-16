package com.jfireframework.dson.metadata;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.dson.JsonProcessor;
import com.jfireframework.dson.StringOutput;
import com.jfireframework.dson.serializer.PropertySerializer;

public class ReflectPropertySerializerFactory implements PropertySerializerFactory
{
    private JsonProcessor jsonProcessor;
    
    public ReflectPropertySerializerFactory(JsonProcessor jsonProcessor)
    {
        this.jsonProcessor = jsonProcessor;
    }
    
    @Override
    public <T> PropertySerializer<T> get(Class<T> type, String property)
    {
        Class<?> ckass = type;
        Field field = null;
        while (ckass != Object.class)
        {
            try
            {
                field = ckass.getDeclaredField(property);
            }
            catch (NoSuchFieldException e)
            {
                ckass = ckass.getSuperclass();
                continue;
            }
            catch (Throwable e)
            {
                throw new JustThrowException(e);
            }
        }
        if (field == null)
        {
            throw new NullPointerException();
        }
        field.setAccessible(true);
        Class<?> fieldType = field.getType();
        PropertySerializer<T> propertySerializer = null;
        if (fieldType == int.class //
                || fieldType == short.class //
                || fieldType == long.class//
                || fieldType == float.class//
                || fieldType == double.class//
                || fieldType == byte.class//
                || fieldType == Byte.class//
                || Number.class.isAssignableFrom(fieldType))
        {
            propertySerializer = new NumberPropertySerializer<T>();
        }
        else if (fieldType == String.class || fieldType == Character.class || fieldType == char.class)
        {
            propertySerializer = new StringProeprtySerializer<T>();
        }
        else if (fieldType == boolean.class || fieldType == Boolean.class)
        {
            propertySerializer = new BooleanPropertySerializer<T>();
        }
        else if (Map.class.isAssignableFrom(fieldType))
        {
            
        }
        else if (Collection.class.isAssignableFrom(fieldType))
        {
            
        }
        else if (Iterator.class.isAssignableFrom(fieldType))
        {
            
        }
        else if (fieldType.isArray())
        {
            
        }
        else
        {
            propertySerializer = new BeanPropertySerializer<T>();
        }
        propertySerializer.initialize(type, property);
        return propertySerializer;
    }
    
    abstract class AbstractPropertySerializer<T> implements PropertySerializer<T>
    {
        private String propertyName;
        private Field  field;
        
        @Override
        public String propertyName()
        {
            return propertyName;
        }
        
        @Override
        public Object propertyValue(T entity)
        {
            try
            {
                return field.get(entity);
            }
            catch (Throwable e)
            {
                throw new JustThrowException(e);
            }
        }
        
        @Override
        public void initialize(Class<T> type, String property)
        {
            Class<?> ckass = type;
            while (ckass != Object.class)
            {
                try
                {
                    field = ckass.getDeclaredField(property);
                }
                catch (NoSuchFieldException e)
                {
                    ckass = ckass.getSuperclass();
                    continue;
                }
                catch (Throwable e)
                {
                    throw new JustThrowException(e);
                }
            }
            propertyName = field.getName();
        }
        
    }
    
    class NumberPropertySerializer<T> extends AbstractPropertySerializer<T>
    {
        
        @Override
        public void serialize(Object propertyValue, StringOutput output)
        {
            output.append(propertyValue);
        }
        
    }
    
    class StringProeprtySerializer<T> extends AbstractPropertySerializer<T>
    {
        
        @Override
        public void serialize(Object propertyValue, StringOutput output)
        {
            output.append('"').append(propertyValue).append('"');
        }
    }
    
    class BooleanPropertySerializer<T> extends AbstractPropertySerializer<T>
    {
        
        @Override
        public void serialize(Object propertyValue, StringOutput output)
        {
            output.append(propertyValue);
        }
        
    }
    
    class BeanPropertySerializer<T> extends AbstractPropertySerializer<T>
    {
        
        @Override
        public void serialize(Object propertyValue, StringOutput output)
        {
            jsonProcessor.serialize(propertyValue, output);
        }
        
    }
}
