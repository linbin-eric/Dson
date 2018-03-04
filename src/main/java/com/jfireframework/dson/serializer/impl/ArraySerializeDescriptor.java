package com.jfireframework.dson.serializer.impl;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import com.jfireframework.dson.serializer.SerializeDescriptor;
import com.jfireframework.dson.serializer.Serializer;
import com.jfireframework.dson.util.StringOutput;

public class ArraySerializeDescriptor implements SerializeDescriptor
{
	
	interface ArraySerializer
	{
		void initialize(Type type);
		
		boolean Serializer(Object entity, StringOutput output);
	}
	
	private Serializer		serializer;
	private ArraySerializer	arraySerializer;
	
	@Override
	public void initialize(Serializer serializer, Type type)
	{
		Class<?> componentType = ((Class<?>) type).getComponentType();
		if (componentType == int.class)
		{
			arraySerializer = new IntArraySerializer();
		}
		else if (componentType == short.class)
		{
			arraySerializer = new ShortArraySerializer();
		}
		else if (componentType == long.class)
		{
			arraySerializer = new LongArraySerializer();
		}
		else if (componentType == float.class)
		{
			arraySerializer = new FloatArraySerializer();
		}
		else if (componentType == double.class)
		{
			arraySerializer = new DoubleArraySerializer();
		}
		else if (componentType == boolean.class)
		{
			arraySerializer = new BooleanArraySerializer();
		}
		else if (componentType == byte.class)
		{
			arraySerializer = new ByteArraySerializer();
		}
		else if (componentType == char.class)
		{
			arraySerializer = new CharArraySerializer();
		}
		else if (componentType == String.class)
		{
			arraySerializer = new StringArraySerializer();
		}
		else if (Modifier.isFinal(componentType.getModifiers()))
		{
			arraySerializer = new FinalArraySerializer();
		}
		else
		{
			arraySerializer = new UnFinalArraySerializer();
		}
	}
	
	@Override
	public boolean serialize(Object entity, StringOutput output)
	{
		return arraySerializer.Serializer(entity, output);
	}
	
	@Override
	public boolean serializeWithoutDoubleQuotes(Object entity, StringOutput output)
	{
		return serialize(entity, output);
	}
	
	abstract class TemplateSerializer implements ArraySerializer
	{
		
		@Override
		public void initialize(Type type)
		{
		}
		
		@Override
		public boolean Serializer(Object entity, StringOutput output)
		{
			if (entity == null)
			{
				return false;
			}
			output.append('[');
			int length = output.length();
			output(entity, output);
			if (length != output.length())
			{
				output.deleteLast();
			}
			output.append(']');
			return true;
		}
		
		abstract void output(Object entity, StringOutput output);
	}
	
	class IntArraySerializer extends TemplateSerializer
	{
		@Override
		void output(Object entity, StringOutput output)
		{
			int[] array = (int[]) entity;
			for (int i : array)
			{
				output.append(i).append(',');
			}
		}
	}
	
	class ShortArraySerializer extends TemplateSerializer
	{
		
		@Override
		void output(Object entity, StringOutput output)
		{
			short[] array = (short[]) entity;
			for (short s : array)
			{
				output.append(s).append(',');
			}
		}
	}
	
	class BooleanArraySerializer extends TemplateSerializer
	{
		
		@Override
		void output(Object entity, StringOutput output)
		{
			boolean[] array = (boolean[]) entity;
			for (boolean b : array)
			{
				output.append(b).append(',');
			}
		}
		
	}
	
	class LongArraySerializer extends TemplateSerializer
	{
		
		@Override
		void output(Object entity, StringOutput output)
		{
			long[] array = (long[]) entity;
			for (long l : array)
			{
				output.append(l).append(',');
			}
		}
		
	}
	
	class FloatArraySerializer extends TemplateSerializer
	{
		
		@Override
		void output(Object entity, StringOutput output)
		{
			float[] array = (float[]) entity;
			for (float f : array)
			{
				output.append(f).append(',');
			}
		}
		
	}
	
	class DoubleArraySerializer extends TemplateSerializer
	{
		
		@Override
		void output(Object entity, StringOutput output)
		{
			double[] array = (double[]) entity;
			for (double d : array)
			{
				output.append(d).append(',');
			}
		}
		
	}
	
	class ByteArraySerializer extends TemplateSerializer
	{
		
		@Override
		void output(Object entity, StringOutput output)
		{
			byte[] array = (byte[]) entity;
			for (byte b : array)
			{
				output.append(b).append(',');
			}
		}
		
	}
	
	class CharArraySerializer extends TemplateSerializer
	{
		
		@Override
		void output(Object entity, StringOutput output)
		{
			char[] array = (char[]) entity;
			for (char c : array)
			{
				output.append(c).append(',');
			}
		}
		
	}
	
	class StringArraySerializer extends TemplateSerializer
	{
		
		@Override
		void output(Object entity, StringOutput output)
		{
			String[] array = (String[]) entity;
			for (String each : array)
			{
				output.appendDoubleQuotes().append(each).appendDoubleQuotes().append(',');
			}
		}
		
	}
	
	class FinalArraySerializer extends TemplateSerializer
	{
		private SerializeDescriptor serializeDescriptor;
		
		@Override
		public void initialize(Type type)
		{
			super.initialize(type);
			serializeDescriptor = serializer.describe(type);
		}
		
		@Override
		void output(Object entity, StringOutput output)
		{
			Object[] array = (Object[]) entity;
			for (Object each : array)
			{
				if (serializeDescriptor.serialize(each, output))
				{
					output.append(',');
				}
			}
		}
		
	}
	
	class UnFinalArraySerializer extends TemplateSerializer
	{
		
		@Override
		public void initialize(Type type)
		{
			super.initialize(type);
		}
		
		@Override
		void output(Object entity, StringOutput output)
		{
			Object[] array = (Object[]) entity;
			for (Object each : array)
			{
				int length = output.length();
				serializer.serialize(each, output);
				if (length != output.length())
				{
					output.append(',');
				}
			}
		}
		
	}
}
