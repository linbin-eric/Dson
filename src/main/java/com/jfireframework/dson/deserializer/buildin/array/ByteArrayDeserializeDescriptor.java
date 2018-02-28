package com.jfireframework.dson.deserializer.buildin.array;

import com.jfireframework.dson.metadata.json.DsonObject;
import com.jfireframework.dson.metadata.json.Element;
import com.jfireframework.dson.metadata.json.JsonArray;

public class ByteArrayDeserializeDescriptor extends BaseArrayDeserializeDescriptor
{
	
	@Override
	public Object deserialize(DsonObject dsonObject)
	{
		JsonArray jsonArray = (JsonArray) dsonObject;
		byte[] array = new byte[jsonArray.getElements().size()];
		int index = 0;
		for (Element element : jsonArray.getElements())
		{
			array[index] = ((Long) element.getValue()).byteValue();
			index += 1;
		}
		return array;
	}
	
	@Override
	protected void checkArrayType(Class<?> arrayType)
	{
		if (arrayType != byte[].class)
		{
			throw new IllegalArgumentException();
		}
	}
	
}
