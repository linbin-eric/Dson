package com.jfireframework.dson.serializer.impl;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import com.jfireframework.dson.JsonProcessor;
import com.jfireframework.dson.StringOutput;
import com.jfireframework.dson.serializer.MapSerializer;

public class MapSerializerImpl<T> implements MapSerializer<T>
{
    private JsonProcessor jsonProcessor;
    
    @Override
    public void serialize(T entity, StringOutput output)
    {
        if (entity == null)
        {
            return;
        }
        output.append('{');
        boolean first = true;
        for (Entry<?, ?> entry : ((Map<?, ?>) entity).entrySet())
        {
            Object value = entry.getValue();
            if (value != null)
            {
                if (first == false)
                {
                    output.append(',');
                }
                output.append('"').append(entry.getKey().toString()).append("\":");
                if (value instanceof String || value instanceof Character)
                {
                    output.append('"').append(value).append('"');
                }
                else if (value instanceof Number //
                        || value instanceof Boolean)
                {
                    output.append(value);
                }
                else
                {
                    jsonProcessor.serialize(value, output);
                }
                first = false;
            }
        }
        output.append('}');
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Set<Object> keys(T entity)
    {
        return ((Map<Object, ?>) entity).keySet();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object value(Object key, T entity)
    {
        return ((Map<Object, Object>) entity).get(key);
    }
    
    @Override
    public void initialize(JsonProcessor jsonProcessor, Class<T> type)
    {
        this.jsonProcessor = jsonProcessor;
    }
    
}
