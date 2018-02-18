package com.jfireframework.dson.serializer.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.dson.JsonProcessor;
import com.jfireframework.dson.StringOutput;
import com.jfireframework.dson.serializer.BeanSerializer;
import com.jfireframework.dson.serializer.PropertySerializer;

public class BeanSerializerImpl implements BeanSerializer
{
    private PropertySerializer[] propertySerializers;
    
    @Override
    public void serialize(Object entity, StringOutput output)
    {
        if (propertySerializers.length == 0)
        {
            output.append("{}");
            return;
        }
        output.append('{');
        boolean isFirst = true;
        for (PropertySerializer each : propertySerializers)
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
            PropertySerializer propertySerializer = jsonProcessor.propertySerializerFactory().get(type, field.getName());
            propertySerializers.add(propertySerializer);
        }
        this.propertySerializers = propertySerializers.toArray(new PropertySerializer[propertySerializers.size()]);
    }
    
}
