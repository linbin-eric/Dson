package com.jfireframework.dson.serializer.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.dson.JsonProcessor;
import com.jfireframework.dson.StringOutput;
import com.jfireframework.dson.serializer.BeanSerializer;
import com.jfireframework.dson.serializer.PropertySerializer;

public class BeanSerializerImpl<T> implements BeanSerializer<T>
{
    private PropertySerializer<T>[] propertySerializers;
    
    @Override
    public void serialize(T entity, StringOutput output)
    {
        if (propertySerializers.length == 0)
        {
            output.append("{}");
            return;
        }
        output.append('{');
        boolean isFirst = true;
        for (PropertySerializer<T> each : propertySerializers)
        {
            Object value = each.propertyValue(entity);
            if (value != null)
            {
                if (isFirst == false)
                {
                    output.append(',');
                }
                output.append('"').append(each.propertyName()).append("\":");
                each.serialize(value, output);
            }
        }
        output.append('}');
    }
    
    @Override
    public PropertySerializer<T>[] propertySerializers()
    {
        return propertySerializers;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void initialize(JsonProcessor jsonProcessor, Class<T> type)
    {
        List<PropertySerializer<T>> propertySerializers = new ArrayList<PropertySerializer<T>>();
        for (Field field : ReflectUtil.getAllFields(type))
        {
            PropertySerializer<T> propertySerializer = jsonProcessor.propertySerializerFactory().get(type, field.getName());
            propertySerializers.add(propertySerializer);
        }
        this.propertySerializers = propertySerializers.toArray(new PropertySerializer[propertySerializers.size()]);
    }
    
}
