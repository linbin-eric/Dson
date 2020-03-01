package com.jfirer.dson.deserializer.buildin.array;

import com.jfirer.dson.metadata.json.DsonObject;
import com.jfirer.dson.metadata.json.Element;
import com.jfirer.dson.metadata.json.JsonArray;

public class IntArrayDeserializeDescriptor extends BaseArrayDeserializeDescriptor
{
	
	@Override
	public Object deserialize(DsonObject dsonObject)
	{
		JsonArray jsonArray = (JsonArray) dsonObject;
		int[]     array     = new int[jsonArray.getElements().size()];
		int       index     = 0;
		for (Element element : jsonArray.getElements())
		{
			array[index] = ((Long) element.getValue()).intValue();
			index += 1;
		}
		return array;
	}
	
	@Override
	protected void checkArrayType(Class<?> arrayType)
	{
		if (arrayType != int[].class)
		{
			throw new IllegalArgumentException();
		}
	}
}
