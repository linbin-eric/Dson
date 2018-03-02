package com.jfireframework.dson.serializer.impl;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import com.jfireframework.dson.serializer.SerializeDescriptor;
import com.jfireframework.dson.serializer.Serializer;
import com.jfireframework.dson.util.StringOutput;

public class MapSerializeDescriptor implements SerializeDescriptor
{
	interface KeyDescriptor
	{
		void initialize(Type type);
		
		void serialize(Entry<?, ?> entry, StringOutput output);
	}
	
	interface ValueDescriptor
	{
		void initialize(Type type);
		
		void serialize(Entry<?, ?> entry, StringOutput output);
	}
	
	private Serializer		serializer;
	private KeyDescriptor	keyDescriptor;
	private ValueDescriptor	valueDescriptor;
	
	@Override
	public boolean serializeWithoutDoubleQuotes(Object entity, StringOutput output)
	{
		return serializeWithoutDoubleQuotes(entity, output);
	}
	
	@Override
	public boolean serialize(Object entity, StringOutput output)
	{
		if (entity == null)
		{
			return false;
		}
		output.append('{');
		boolean serialized = false;
		for (Entry<?, ?> entry : ((Map<?, ?>) entity).entrySet())
		{
			if (serialize(entry, output))
			{
				serialized = true;
				output.append(',');
			}
		}
		if (serialized)
		{
			output.deleteLast();
		}
		output.append('}');
		return true;
	}
	
	private boolean serialize(Entry<?, ?> entry, StringOutput output)
	{
		Object value = entry.getValue();
		if (value == null)
		{
			return false;
		}
		keyDescriptor.serialize(entry, output);
		valueDescriptor.serialize(entry, output);
		return true;
	}
	
	@Override
	public void initialize(Serializer serializer, Type type)
	{
		this.serializer = serializer;
		if (type instanceof ParameterizedType)
		{
			Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
			Type keyType = actualTypeArguments[0];
			if (keyType instanceof Class<?>)
			{
				if (keyType.equals(String.class))
				{
					keyDescriptor = new StringKeyDescriptor();
				}
				else if (Modifier.isFinal(((Class<?>) keyType).getModifiers()))
				{
					keyDescriptor = new FinalObjectKeyDescriptor();
				}
				else
				{
					keyDescriptor = new UnFinalObjectKeyDescriptor();
				}
			}
			else
			{
				keyDescriptor = new UnFinalObjectKeyDescriptor();
			}
			keyDescriptor.initialize(keyType);
			Type valueType = actualTypeArguments[1];
			if (valueType instanceof Class<?>)
			{
				if (valueType.equals(String.class))
				{
					valueDescriptor = new StringValueDescriptor();
				}
				else if (Number.class.isAssignableFrom((Class<?>) valueType) || valueType.equals(Boolean.class))
				{
					valueDescriptor = new DirectValueDescriptror();
				}
				else if (Modifier.isFinal(((Class<?>) valueType).getModifiers()))
				{
					valueDescriptor = new FinalObjectValueDescriptor();
				}
				else
				{
					valueDescriptor = new UnFinalObjectValueDescriptor();
				}
				valueDescriptor.initialize(valueType);
			}
		}
		else if (type instanceof Class<?>)
		{
			keyDescriptor = new UnFinalObjectKeyDescriptor();
			valueDescriptor = new UnFinalObjectValueDescriptor();
		}
		else
		{
			throw new IllegalArgumentException();
		}
	}
	
	class StringKeyDescriptor implements KeyDescriptor
	{
		
		@Override
		public void initialize(Type type)
		{
		}
		
		@Override
		public void serialize(Entry<?, ?> entry, StringOutput output)
		{
			output.appendDoubleQuotes().append((String) entry.getKey()).appendDoubleQuotes().append(':');
		}
		
	}
	
	class FinalObjectKeyDescriptor implements KeyDescriptor
	{
		
		private SerializeDescriptor serializeDescriptor;
		
		@Override
		public void initialize(Type type)
		{
			serializeDescriptor = serializer.describe(type);
		}
		
		@Override
		public void serialize(Entry<?, ?> entry, StringOutput output)
		{
			output.appendDoubleQuotes();
			serializeDescriptor.serializeWithoutDoubleQuotes(entry.getKey(), output);
			output.appendDoubleQuotes().append(':');
		}
		
	}
	
	class UnFinalObjectKeyDescriptor implements KeyDescriptor
	{
		
		@Override
		public void serialize(Entry<?, ?> entry, StringOutput output)
		{
			output.appendDoubleQuotes();
			serializer.serializeWithoutDoubleQuotes(entry.getKey(), output);
			output.appendDoubleQuotes().append(':');
		}
		
		@Override
		public void initialize(Type type)
		{
		}
		
	}
	
	class DirectValueDescriptror implements ValueDescriptor
	{
		
		@Override
		public void initialize(Type type)
		{
			
		}
		
		@Override
		public void serialize(Entry<?, ?> entry, StringOutput output)
		{
			output.append((Number) entry.getValue());
		}
		
	}
	
	class StringValueDescriptor implements ValueDescriptor
	{
		
		@Override
		public void initialize(Type type)
		{
			
		}
		
		@Override
		public void serialize(Entry<?, ?> entry, StringOutput output)
		{
			output.appendDoubleQuotes().append((String) entry.getValue()).appendDoubleQuotes();
		}
	}
	
	class FinalObjectValueDescriptor implements ValueDescriptor
	{
		private SerializeDescriptor serializeDescriptor;
		
		@Override
		public void initialize(Type type)
		{
			serializeDescriptor = serializer.describe(type);
		}
		
		@Override
		public void serialize(Entry<?, ?> entry, StringOutput output)
		{
			serializeDescriptor.serialize(entry.getValue(), output);
		}
		
	}
	
	class UnFinalObjectValueDescriptor implements ValueDescriptor
	{
		
		@Override
		public void initialize(Type type)
		{
			
		}
		
		@Override
		public void serialize(Entry<?, ?> entry, StringOutput output)
		{
			serializer.serialize(entry.getValue(), output);
		}
		
	}
	
}
