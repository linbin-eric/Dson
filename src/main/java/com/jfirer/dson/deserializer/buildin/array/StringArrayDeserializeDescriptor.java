package com.jfirer.dson.deserializer.buildin.array;

import com.jfirer.dson.metadata.json.DsonObject;
import com.jfirer.dson.metadata.json.Element;
import com.jfirer.dson.metadata.json.JsonArray;

public class StringArrayDeserializeDescriptor extends BaseArrayDeserializeDescriptor
{
	
	@Override
	public Object deserialize(DsonObject dsonObject)
	{
		JsonArray jsonArray = (JsonArray) dsonObject;
		String[]  array     = new String[jsonArray.getElements().size()];
		int       index     = 0;
		for (Element element : jsonArray.getElements())
		{
			array[index] = ((String) element.getValue());
			index += 1;
		}
		return array;
	}
	
	@Override
	protected void checkArrayType(Class<?> arrayType)
	{
		if (arrayType != String[].class)
		{
			throw new IllegalArgumentException();
		}
	}
	
}
