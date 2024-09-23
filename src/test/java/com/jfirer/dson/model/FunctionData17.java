package com.jfirer.dson.model;

import com.jfirer.dson.reader.DeSerializeDefinition;
import com.jfirer.dson.reader.impl.ReadObjectAsString;

public class FunctionData17
{
    @DeSerializeDefinition(ReadObjectAsString.class)
    private Object value;

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }
}
