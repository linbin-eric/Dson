package com.jfireframework.dson.metadata;

public class Entry
{
	String			name;
	Object			value;
	JsonValueType	valueType;
	
	public Entry(String name, Object value, JsonValueType valueType)
	{
		this.name = name;
		this.value = value;
		this.valueType = valueType;
	}
	
}
