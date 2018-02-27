package com.jfireframework.dson.metadata.json;

import java.util.ArrayList;
import java.util.List;

public class JsonCollection implements DsonObject
{
	List<Entry> entries = new ArrayList<Entry>();
	
	public List<Entry> getEntries()
	{
		return entries;
	}
	
	public void add(String name, Object value, JsonValueType valueType)
	{
		entries.add(new Entry(name, value, valueType));
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append('{');
		for (Entry entry : entries)
		{
			builder.append('"').append(entry.getName()).append("\":");
			switch (entry.getValueType())
			{
				case STRING:
					builder.append('"').append(entry.getValue()).append('"');
					break;
				case NUMBER_DOUBLE:
				case NUMBER_LONG:
					builder.append(entry.getValue());
					break;
				case BOOLEAN:
					builder.append(entry.getValue());
					break;
				case COLLECTION:
					builder.append(entry.value);
					break;
				case ARRAY:
					builder.append(entry.value);
				default:
					break;
			}
			builder.append(',');
		}
		if (entries.isEmpty() == false)
		{
			builder.deleteCharAt(builder.length() - 1);
		}
		builder.append('}');
		return builder.toString();
	}
}
