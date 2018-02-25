package com.jfireframework.dson.deserializer.token;

import java.util.ArrayList;
import java.util.List;

class JsonCollection
{
    List<Entry> entries = new ArrayList<Entry>();
    
    public List<Entry> getEntries()
    {
        return entries;
    }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append('{');
        for (Entry entry : entries)
        {
            builder.append('"').append(entry.name).append("\":");
            switch (entry.valueType)
            {
                case STRING:
                    builder.append('"').append(entry.value).append('"');
                    break;
                case NUMBER:
                    builder.append(entry.value);
                    break;
                case BOOLEAN:
                    builder.append(entry.value);
                    break;
                case COLLECTION:
                    builder.append(entry.value);
                    break;
                default:
                    break;
            }
            builder.append(',');
        }
        if (entries.isEmpty() == false)
        {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append('}');
        return builder.toString();
    }
}