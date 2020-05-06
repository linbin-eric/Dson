package com.jfirer.dson.model;

import com.jfirer.dson.strategy.DeSerializeDefinition;
import com.jfirer.dson.strategy.buildin.ReadObjectAsString;

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
