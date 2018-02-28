package com.jfireframework.dson.deserializer.buildin.array;

import com.jfireframework.dson.metadata.json.DsonObject;
import com.jfireframework.dson.metadata.json.Element;
import com.jfireframework.dson.metadata.json.JsonArray;

public class BooleanArrayDeserializeDescriptor extends BaseArrayDeserializeDescriptor
{
	
	@Override
	public Object deserialize(DsonObject dsonObject)
	{
		JsonArray jsonArray = (JsonArray) dsonObject;
		boolean[] array = new boolean[jsonArray.getElements().size()];
		int index = 0;
		for (Element element : jsonArray.getElements())
		{
			array[index] = ((Boolean) element.getValue()).booleanValue();
			index += 1;
		}
		return array;
	}
	
	@Override
	protected void checkArrayType(Class<?> arrayType)
	{
		if (arrayType != boolean[].class)
		{
			throw new IllegalArgumentException();
		}
	}
	
}
