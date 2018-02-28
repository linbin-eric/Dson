package com.jfireframework.dson.metadata.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonCollection implements DsonObject
{
	List<Entry>			entries	= new ArrayList<Entry>();
	Map<String, Entry>	map ;
	
	public Collection<Entry> getEntries()
	{
		return entries;
	}
	
	public Map<String, Entry> getMap()
	{
		if (map != null)
		{
			return map;
		}
		map = new HashMap<String, Entry>();
		for (Entry entry : entries)
		{
			map.put(entry.getName(), entry);
		}
		return map;
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
