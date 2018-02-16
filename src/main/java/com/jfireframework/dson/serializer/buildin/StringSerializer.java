package com.jfireframework.dson.serializer.buildin;

import com.jfireframework.dson.JsonProcessor;
import com.jfireframework.dson.StringOutput;
import com.jfireframework.dson.serializer.Serializer;

public class StringSerializer implements Serializer<String>
{
    
    @Override
    public void initialize(JsonProcessor jsonProcessor, Class<String> type)
    {
        
    }
    
    @Override
    public void serialize(String entity, StringOutput output)
    {
        output.appendDoubleQuotes().append(entity).appendDoubleQuotes();
    }
    
}
