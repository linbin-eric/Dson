package com.jfireframework.dson.serializer.buildin;

import java.lang.reflect.Type;
import java.util.Map;
import com.jfireframework.dson.serializer.TypeWriter;
import com.jfireframework.dson.serializer.JsonWriter;
import com.jfireframework.dson.util.WriterUtil;

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
