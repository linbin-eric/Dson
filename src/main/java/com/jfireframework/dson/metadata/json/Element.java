package com.jfireframework.dson.metadata.json;

public class Element
{
	final Object		value;
	final JsonValueType	valueType;
	
	public Element(Object value, JsonValueType valueType)
	{
		this.value = value;
		this.valueType = valueType;
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
