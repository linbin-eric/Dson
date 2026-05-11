package cc.jfire.dson.model;

import cc.jfire.dson.writer.SerializeDefinition;
import cc.jfire.dson.writer.TypeWriter;

public class FunctionData16
{
    @SerializeDefinition(NameSeri.class)
    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public static final class NameSeri implements TypeWriter
    {
        @Override
        public void toJson(Object entity, StringBuilder output)
        {
            output.append('"').append("123").append('"');
        }

        @Override
        public Object toJsonValue(Object entity)
        {
            return "123";
        }
    }
}
