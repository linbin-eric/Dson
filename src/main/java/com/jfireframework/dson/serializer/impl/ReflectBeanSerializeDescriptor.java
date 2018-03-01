package com.jfireframework.dson.serializer.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.dson.serializer.SerializeDescriptor;
import com.jfireframework.dson.serializer.Serializer;
import com.jfireframework.dson.util.StringOutput;

public class ReflectBeanSerializeDescriptor implements SerializeDescriptor
{
	
	interface PropertySerializer
	{
		/**
		 * 如果有值，则序列化，返回true；否则返回false
		 * 
		 * @param host
		 * @param output
		 * @return
		 */
		boolean serialize(Object host, StringOutput output);
	}
	
	private PropertySerializer[]	propertySerializers;
	private Serializer				serializer;
	
	@Override
	public boolean serialize(Object entity, StringOutput output)
	{
		if (propertySerializers.length == 0 || entity == null)
		{
			return false;
		}
		output.append('{');
		int length = output.length();
		for (PropertySerializer each : propertySerializers)
		{
			if (each.serialize(entity, output))
			{
				output.append(',');
			}
		}
		if (length != output.length())
		{
			output.deleteLast();
		}
		output.append('}');
		return true;
	}
	
	@Override
	public void initialize(Serializer jsonProcessor, Type type)
	{
		List<PropertySerializer> list = new ArrayList<PropertySerializer>();
		for (Field field : ReflectUtil.getAllFields((Class<?>) type))
		{
			if (field.getName().contains("this"))
			{
				continue;
			}
			Class<?> fieldType = field.getType();
			AbstractPropertySerializer propertySerializer;
			if (fieldType == int.class //
			        || fieldType == short.class //
			        || fieldType == long.class//
			        || fieldType == float.class//
			        || fieldType == double.class//
			        || fieldType == byte.class//
			        || fieldType == Byte.class//
			        || Number.class.isAssignableFrom(fieldType))
			{
				propertySerializer = new NumberPropertySerializer();
			}
			else if (fieldType == String.class || fieldType == Character.class || fieldType == char.class)
			{
				propertySerializer = new StringProeprtySerializer();
			}
			else if (fieldType == boolean.class || fieldType == Boolean.class)
			{
				propertySerializer = new BooleanPropertySerializer();
			}
			else if (Map.class.isAssignableFrom(fieldType))
			{
				propertySerializer = new MapPropertySerializer();
			}
			else if (Collection.class.isAssignableFrom(fieldType))
			{
				propertySerializer = new CollectionPropertySerializer();
			}
			else if (fieldType.isArray())
			{
				propertySerializer = new ArrayPropertySerializer();
			}
			else if (Modifier.isFinal(fieldType.getModifiers()))
			{
				propertySerializer = new FinalBeanPropertySerializer();
			}
			else
			{
				propertySerializer = new UnFinalBeanPropertySerializer();
			}
			propertySerializer.initialize(field);
			list.add(propertySerializer);
		}
		this.propertySerializers = list.toArray(new PropertySerializer[list.size()]);
	}
	
	abstract class AbstractPropertySerializer implements PropertySerializer
	{
		protected String	propertyName;
		protected Field		field;
		
		public void initialize(Field field)
		{
			this.field = field;
			propertyName = field.getName();
			field.setAccessible(true);
		}
		
		public boolean serialize(Object entity, StringOutput output)
		{
			try
			{
				Object propertyValue = field.get(entity);
				if (propertyValue == null)
				{
					return false;
				}
				output.appendDoubleQuotes().append(propertyName).appendDoubleQuotes().append(':');
				outputPropertyValue(propertyValue, output);
				return true;
			}
			catch (Exception e)
			{
				throw new JustThrowException(e);
			}
		}
		
		protected abstract void outputPropertyValue(Object propertyValue, StringOutput output);
	}
	
	class NumberPropertySerializer extends AbstractPropertySerializer
	{
		
		@Override
		protected void outputPropertyValue(Object propertyValue, StringOutput output)
		{
			output.append((Number) propertyValue);
		}
		
	}
	
	class StringProeprtySerializer extends AbstractPropertySerializer
	{
		
		@Override
		protected void outputPropertyValue(Object propertyValue, StringOutput output)
		{
			output.appendDoubleQuotes().append((String) propertyValue).appendDoubleQuotes();
		}
		
	}
	
	class BooleanPropertySerializer extends AbstractPropertySerializer
	{
		
		@Override
		protected void outputPropertyValue(Object propertyValue, StringOutput output)
		{
			output.append((Boolean) propertyValue);
		}
		
	}
	
	class FinalBeanPropertySerializer extends AbstractPropertySerializer
	{
		private SerializeDescriptor serializeDescriptor;
		
		@Override
		public void initialize(Field field)
		{
			super.initialize(field);
			serializeDescriptor = serializer.describe(field.getType());
			
		}
		
		@Override
		protected void outputPropertyValue(Object propertyValue, StringOutput output)
		{
			serializeDescriptor.serialize(propertyValue, output);
		}
		
	}
	
	class UnFinalBeanPropertySerializer extends AbstractPropertySerializer
	{
		
		@Override
		protected void outputPropertyValue(Object propertyValue, StringOutput output)
		{
			serializer.serialize(propertyValue, output);
		}
		
	}
	
	class MapPropertySerializer extends AbstractPropertySerializer
	{
		private SerializeDescriptor serializeDescriptor;
		
		@Override
		public void initialize(Field field)
		{
			super.initialize(field);
			serializeDescriptor = serializer.describe(field.getGenericType());
		}
		
		@Override
		protected void outputPropertyValue(Object propertyValue, StringOutput output)
		{
			serializeDescriptor.serialize(propertyValue, output);
		}
	}
	
	class CollectionPropertySerializer extends AbstractPropertySerializer
	{
		private SerializeDescriptor serializeDescriptor;
		
		@Override
		public void initialize(Field field)
		{
			super.initialize(field);
			serializeDescriptor = serializer.describe(field.getGenericType());
		}
		
		@Override
		protected void outputPropertyValue(Object propertyValue, StringOutput output)
		{
			serializeDescriptor.serialize(propertyValue, output);
		}
		
	}
	
	class ArrayPropertySerializer extends AbstractPropertySerializer
	{
		private SerializeDescriptor serializeDescriptor;
		
		@Override
		public void initialize(Field field)
		{
			super.initialize(field);
			serializeDescriptor = serializer.describe(field.getGenericType());
		}
		
		@Override
		protected void outputPropertyValue(Object propertyValue, StringOutput output)
		{
			serializeDescriptor.serialize(propertyValue, output);
		}
		
	}
}
