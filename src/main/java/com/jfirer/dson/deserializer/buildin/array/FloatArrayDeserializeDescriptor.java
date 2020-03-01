package com.jfirer.dson.deserializer.buildin.array;

import com.jfirer.dson.metadata.json.DsonObject;
import com.jfirer.dson.metadata.json.Element;
import com.jfirer.dson.metadata.json.JsonArray;

public class FloatArrayDeserializeDescriptor extends BaseArrayDeserializeDescriptor
{
	
	@Override
	public Object deserialize(DsonObject dsonObject)
	{
		JsonArray jsonArray = (JsonArray) dsonObject;
		float[]   array     = new float[jsonArray.getElements().size()];
		int       index     = 0;
		for (Element element : jsonArray.getElements())
		{
			array[index] = ((Double) element.getValue()).floatValue();
			index += 1;
		}
		return array;
	}
	
	@Override
	protected void checkArrayType(Class<?> arrayType)
	{
		if (arrayType != float[].class)
		{
			throw new IllegalArgumentException();
		}
	}
	
}
