package com.jfireframework.dson.serializer.impl;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import com.jfireframework.dson.serializer.SerializeDescriptor;
import com.jfireframework.dson.serializer.Serializer;
import com.jfireframework.dson.util.StringOutput;

public class CollectionSerializeDescriptor implements SerializeDescriptor
{
	interface ElementSerializer
	{
		void initialize(Type elementType);
		
		void serialize(Object element, StringOutput output);
	}
	
	private Serializer			serializer;
	private ElementSerializer	elementSerializer;
	
	@Override
	public void initialize(Serializer serializer, Type type)
	{
		this.serializer = serializer;
		if (type instanceof Class<?>)
		{
			elementSerializer = new UnfinalElementSerializer();
		}
		else if (type instanceof ParameterizedType)
		{
			Type elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
			if (elementType instanceof Class<?>)
			{
				if (Modifier.isFinal(((Class<?>) elementType).getModifiers()))
				{
					elementSerializer = new FinalElementSerializer();
				}
				else
				{
					elementSerializer = new UnfinalElementSerializer();
				}
			}
			else if (elementType instanceof ParameterizedType)
			{
				elementSerializer = new UnfinalElementSerializer();
			}
			else
			{
				throw new IllegalArgumentException();
			}
			elementSerializer.initialize(elementType);
		}
		else
		{
			throw new IllegalArgumentException();
		}
	}
	
	@Override
	public void serializeWithoutDoubleQuotes(Object entity, StringOutput output)
	{
		serialize(entity, output);
	}
	
	@Override
	public void serialize(Object entity, StringOutput output)
	{
		if (entity == null)
		{
			return;
		}
		Collection<?> collection = (Collection<?>) entity;
		output.append('[');
		int length = output.length();
		for (Object each : collection)
		{
			if (each != null)
			{
				elementSerializer.serialize(each, output);
				output.append(',');
			}
		}
		if (output.length() != length)
		{
			output.deleteLast();
		}
		output.append(']');
	}
	
	class FinalElementSerializer implements ElementSerializer
	{
		private SerializeDescriptor serializeDescriptor;
		
		@Override
		public void initialize(Type elementType)
		{
			serializeDescriptor = serializer.describe(elementType);
		}
		
		@Override
		public void serialize(Object element, StringOutput output)
		{
			serializeDescriptor.serialize(element, output);
		}
		
	}
	
	class UnfinalElementSerializer implements ElementSerializer
	{
		@Override
		public void initialize(Type elementType)
		{
			
		}
		
		@Override
		public void serialize(Object element, StringOutput output)
		{
			serializer.serialize(element, output);
		}
		
	}
	
}
