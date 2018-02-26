package com.jfireframework.dson.metadata;

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
			builder.append('"').append(entry.name).append("\":");
			switch (entry.valueType)
			{
				case STRING:
					builder.append('"').append(entry.value).append('"');
					break;
				case NUMBER:
					builder.append(entry.value);
					break;
				case BOOLEAN:
					builder.append(entry.value);
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
