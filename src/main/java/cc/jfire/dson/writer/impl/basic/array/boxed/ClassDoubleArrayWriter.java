package cc.jfire.dson.writer.impl.basic.array.boxed;

import cc.jfire.dson.writer.TypeWriter;

public class ClassDoubleArrayWriter implements TypeWriter
{
    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        output.append('[');
        boolean hasComma = false;
        for (Double element : (Double[]) entity)
        {
            if (element != null)
            {
                output.append(element).append(',');
                hasComma = true;
            }
        }
        if (hasComma)
        {
            output.setLength(output.length() - 1);
        }
        output.append(']');
    }
}
