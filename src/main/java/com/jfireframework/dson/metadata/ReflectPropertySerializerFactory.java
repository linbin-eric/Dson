package com.jfireframework.dson.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.dson.JsonProcessor;
import com.jfireframework.dson.serializer.BeanSerializer;
import com.jfireframework.dson.serializer.PropertySerializer;
import com.jfireframework.dson.util.StringOutput;

public class ReflectPropertySerializerFactory implements PropertySerializerFactory
{
	private JsonProcessor jsonProcessor;
	
	@Override
	public void initialize(JsonProcessor jsonProcessor)
	{
		this.jsonProcessor = jsonProcessor;
	}
	
	@Override
	public PropertySerializer get(Class<?> type, String property)
	{
		Class<?> ckass = type;
		Field field = null;
		while (ckass != Object.class)
		{
			try
			{
				field = ckass.getDeclaredField(property);
				break;
			}
			catch (NoSuchFieldException e)
			{
				ckass = ckass.getSuperclass();
				continue;
			}
			catch (Throwable e)
			{
				throw new JustThrowException(e);
			}
		}
		if (field == null)
		{
			throw new NullPointerException();
		}
		field.setAccessible(true);
		Class<?> fieldType = field.getType();
		PropertySerializer propertySerializer = null;
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
			
		}
		else if (Collection.class.isAssignableFrom(fieldType))
		{
			
		}
		else if (Iterator.class.isAssignableFrom(fieldType))
		{
			
		}
		else if (fieldType.isArray())
		{
			
		}
		else if (Modifier.isFinal(fieldType.getModifiers()))
		{
			propertySerializer = new FinalBeanPropertySerializer();
		}
		else
		{
			propertySerializer = new UnFinalBeanPropertySerializer();
		}
		propertySerializer.initialize(type, property);
		return propertySerializer;
	}
	
	abstract class AbstractPropertySerializer implements PropertySerializer
	{
		protected String	propertyName;
		protected Field		field;
		
		@Override
		public void initialize(Class<?> type, String property)
		{
			Class<?> ckass = type;
			while (ckass != Object.class)
			{
				try
				{
					field = ckass.getDeclaredField(property);
					break;
				}
				catch (NoSuchFieldException e)
				{
					ckass = ckass.getSuperclass();
					continue;
				}
				catch (Throwable e)
				{
					throw new JustThrowException(e);
				}
			}
			propertyName = field.getName();
			field.setAccessible(true);
		}
		
	}
	
	class NumberPropertySerializer extends AbstractPropertySerializer
	{
		
		@Override
		public boolean serialize(Object entity, StringOutput output)
		{
			try
			{
				Number number = (Number) field.get(entity);
				if (number == null)
				{
					return false;
				}
				output.appendDoubleQuotes().append(propertyName).appendDoubleQuotes().append(':').append(number);
				return true;
			}
			catch (Throwable e)
			{
				throw new JustThrowException(e);
			}
		}
		
	}
	
	class StringProeprtySerializer extends AbstractPropertySerializer
	{
		
		@Override
		public boolean serialize(Object entity, StringOutput output)
		{
			try
			{
				String value = (String) field.get(entity);
				if (value == null)
				{
					return false;
				}
				output.appendDoubleQuotes().append(propertyName).appendDoubleQuotes().append(':').appendDoubleQuotes().append(value).appendDoubleQuotes();
				return true;
			}
			catch (Exception e)
			{
				throw new JustThrowException(e);
			}
		}
	}
	
	class BooleanPropertySerializer extends AbstractPropertySerializer
	{
		
		@Override
		public boolean serialize(Object entity, StringOutput output)
		{
			try
			{
				Boolean value = (Boolean) field.get(entity);
				if (value == null)
				{
					return false;
				}
				output.appendDoubleQuotes().append(propertyName).appendDoubleQuotes().append(':').append(value);
				return true;
			}
			catch (Exception e)
			{
				throw new JustThrowException(e);
			}
		}
		
	}
	
	class FinalBeanPropertySerializer extends AbstractPropertySerializer
	{
		private BeanSerializer beanSerializer;
		
		@Override
		public void initialize(Class<?> type, String property)
		{
			super.initialize(type, property);
			try
			{
				beanSerializer = jsonProcessor.beanSerializerClass().newInstance();
				beanSerializer.initialize(jsonProcessor, type);
			}
			catch (Exception e)
			{
				throw new JustThrowException(e);
			}
		}
		
		@Override
		public boolean serialize(Object entity, StringOutput output)
		{
			try
			{
				Object value = field.get(entity);
				if (value == null)
				{
					return false;
				}
				output.appendDoubleQuotes().append(propertyName).appendDoubleQuotes().append(':');
				beanSerializer.serialize(value, output);
				return true;
			}
			catch (Exception e)
			{
				throw new JustThrowException(e);
			}
		}
		
	}
	
	class UnFinalBeanPropertySerializer extends AbstractPropertySerializer
	{
		
		@Override
		public boolean serialize(Object entity, StringOutput output)
		{
			try
			{
				Object value = field.get(entity);
				if (value == null)
				{
					return false;
				}
				output.appendDoubleQuotes().append(propertyName).appendDoubleQuotes().append(':');
				jsonProcessor.serialize(value, output);
				return true;
			}
			catch (Exception e)
			{
				throw new JustThrowException(e);
			}
		}
		
	}
	
}
