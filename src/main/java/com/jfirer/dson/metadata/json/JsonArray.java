package com.jfirer.dson.metadata.json;

import java.util.ArrayList;
import java.util.List;

public class JsonArray implements DsonObject
{
	List<Element> elements = new ArrayList<Element>();
	
	public void add(Object value, JsonValueType valueType)
	{
		elements.add(new Element(value, valueType));
	}
	
	public List<Element> getElements()
	{
		return elements;
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		for (Element element : elements)
		{
			switch (element.valueType)
			{
				case STRING:
					builder.append('"').append(element.value).append('"').append(',');
					break;
				case NUMBER_DOUBLE:
				case NUMBER_LONG:
					builder.append(element.value).append(',');
					break;
				case BOOLEAN:
					builder.append(element.value).append(',');
					break;
				case ARRAY:
					builder.append(element.value).append(',');
					break;
				case COLLECTION:
					builder.append(element.value).append(',');
					break;
				default:
					break;
			}
		}
		if (elements.isEmpty() == false)
		{
			builder.deleteCharAt(builder.length() - 1);
		}
		builder.append(']');
		return builder.toString();
	}
}
