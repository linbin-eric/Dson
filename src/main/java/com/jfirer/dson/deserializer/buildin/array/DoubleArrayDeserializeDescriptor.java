package com.jfirer.dson.deserializer.buildin.array;

import com.jfirer.dson.metadata.json.DsonObject;
import com.jfirer.dson.metadata.json.Element;
import com.jfirer.dson.metadata.json.JsonArray;

public class DoubleArrayDeserializeDescriptor extends BaseArrayDeserializeDescriptor
{
	
	@Override
	public Object deserialize(DsonObject dsonObject)
	{
		JsonArray jsonArray = (JsonArray) dsonObject;
		double[]  array     = new double[jsonArray.getElements().size()];
		int       index     = 0;
		for (Element element : jsonArray.getElements())
		{
			array[index] = ((Double) element.getValue()).doubleValue();
			index += 1;
		}
		return array;
	}
	
	@Override
	protected void checkArrayType(Class<?> arrayType)
	{
		if (arrayType != double[].class)
		{
			throw new IllegalArgumentException();
		}
	}
	
}
