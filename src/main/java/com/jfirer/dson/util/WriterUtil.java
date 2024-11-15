package com.jfirer.dson.util;

public class WriterUtil
{
    public static void writeString(StringBuilder output, String value)
    {
        boolean hasSpical = false;
        if (value.indexOf('"') >= 0 || value.indexOf('\n') >= 0 || value.indexOf('\r') >= 0 || value.indexOf('\t') >= 0)
        {
            hasSpical = true;
        }
        if (hasSpical)
        {
            int  len = value.length();
            char c;
            int  off = 0;
            while (off < len)
            {
                c = value.charAt(off);
                if (c == '"')
                {
                    output.append("\\\"");
                }
                else if (c == '\r')
                {
                    output.append("\\r");
                }
                else if (c == '\n')
                {
                    output.append("\\n");
                }
                else if (c == '\\')
                {
                    output.append('\\').append('\\');
                }
                else if (c == '\t')
                {
                    output.append("\\t");
                }
                else
                {
                    output.append(c);
                }
                off += 1;
            }
        }
        else
        {
            output.append(value);
        }
    }
}
