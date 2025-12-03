package cc.jfire.dson.model;

import cc.jfire.dson.reader.DeSerializeDefinition;
import cc.jfire.dson.reader.impl.ReadObjectAsString;

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
