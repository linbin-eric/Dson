package com.jfireframework.dson.serializer.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.dson.JsonProcessor;
import com.jfireframework.dson.serializer.BeanSerializer;
import com.jfireframework.dson.serializer.PropertySerializer;
import com.jfireframework.dson.util.StringOutput;

public class BeanSerializerImpl implements BeanSerializer
{
    private PropertySerializer[] propertySerializers;
    
    @Override
    public boolean serialize(Object entity, StringOutput output)
    {
        if (propertySerializers.length == 0 || entity == null)
        {
            return false;
        }
        output.append('{');
        int length = output.length();
        for (PropertySerializer each : propertySerializers)
        {
            if (each.serialize(entity, output))
            {
                output.append(',');
            }
        }
        if (length != output.length())
        {
            output.deleteLast();
        }
        output.append('}');
        return true;
    }
    
    @Override
    public PropertySerializer[] propertySerializers()
    {
        return propertySerializers;
    }
    
    @Override
    public void initialize(JsonProcessor jsonProcessor, Class<?> type)
    {
        List<PropertySerializer> propertySerializers = new ArrayList<PropertySerializer>();
        for (Field field : ReflectUtil.getAllFields(type))
        {
            if (field.getName().contains("this"))
            {
                continue;
            }
            PropertySerializer propertySerializer = jsonProcessor.propertySerializerFactory().get(type, field.getName());
            propertySerializer.initialize(type, field.getName());
            propertySerializers.add(propertySerializer);
        }
        this.propertySerializers = propertySerializers.toArray(new PropertySerializer[propertySerializers.size()]);
    }
    
}
