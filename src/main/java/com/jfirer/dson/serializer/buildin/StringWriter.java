package com.jfirer.dson.serializer.buildin;

import com.jfirer.dson.serializer.JsonWriter;
import com.jfirer.dson.serializer.TypeWriter;
import com.jfirer.dson.util.WriterUtil;

import java.lang.reflect.Type;

public class StringWriter implements TypeWriter
{
	@Override
	public void initialize(JsonWriter writer, Type type)
	{
	}

	@Override
	public void toJson(Object entity, StringBuilder output)
	{
		if (entity == null)
		{
			return;
		}
		output.append('"');
		WriterUtil.writeString(output, (String) entity);
		output.append('"');
	}
}
