package com.jfireframework.dson.metadata.json;

public class Entry
{
	protected final String			name;
	protected final Object			value;
	protected final JsonValueType	valueType;
	
	public Entry(String name, Object value, JsonValueType valueType)
	{
		this.name = name;
		this.value = value;
		this.valueType = valueType;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Object getValue()
	{
		return value;
	}
	
	public JsonValueType getValueType()
	{
		return valueType;
	}
	
}
