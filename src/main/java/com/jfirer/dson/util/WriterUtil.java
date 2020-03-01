package com.jfirer.dson.util;

public class WriterUtil
{
	public static void writeString(StringBuilder output, String value)
	{
		int len = value.length();
		char c;
		int off = 0;
		while (off < len)
		{
			c = value.charAt(off);
			if (c == '"')
			{
				output.append("\\\"");
			}
			else if (c == '\\')
			{
				output.append('\\').append('\\');
			}
			else
			{
				output.append(c);
			}
			off += 1;
		}
	}
}
