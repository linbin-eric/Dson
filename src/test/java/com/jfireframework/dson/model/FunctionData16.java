package com.jfireframework.dson.model;

import java.lang.reflect.Type;
import java.util.Map;
import com.jfireframework.dson.serializer.SerializeDescriptor;
import com.jfireframework.dson.serializer.Serializer;
import com.jfireframework.dson.strategy.SerializeDefinition;
import com.jfireframework.dson.util.StringOutput;

public class FunctionData16
{
	@SerializeDefinition(NameSeri.class)
	private String name;
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public static final class NameSeri implements SerializeDescriptor
	{
		
		@Override
		public void initialize(Serializer serializer, Type type, Map<Type, SerializeDescriptor> map)
		{
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void serialize(Object entity, StringOutput output)
		{
			output.append('"').append("123").append('"');
		}
		
		@Override
		public void serializeWithoutDoubleQuotes(Object entity, StringOutput output)
		{
			output.append("123");
		}
		
	}
}
