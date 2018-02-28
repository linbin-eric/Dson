package com.jfireframework.dson.deserializer.buildin.array;

import com.jfireframework.dson.metadata.json.DsonObject;
import com.jfireframework.dson.metadata.json.Element;
import com.jfireframework.dson.metadata.json.JsonArray;

public class CharArrayDeserializeDescriptor extends BaseArrayDeserializeDescriptor
{
	
	@Override
	public Object deserialize(DsonObject dsonObject)
	{
		JsonArray jsonArray = (JsonArray) dsonObject;
		char[] array = new char[jsonArray.getElements().size()];
		int index = 0;
		for (Element element : jsonArray.getElements())
		{
			array[index] = ((String) element.getValue()).charAt(0);
			index += 1;
		}
		return array;
	}
	
	@Override
	protected void checkArrayType(Class<?> arrayType)
	{
		if (arrayType != char[].class)
		{
			throw new IllegalArgumentException();
		}
	}
	
}
