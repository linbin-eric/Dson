package com.jfireframework.dson.serializer.property;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.dson.JsonProcessor;
import com.jfireframework.dson.metadata.PropertySerializerFactory;
import com.jfireframework.dson.serializer.ArraySerializer;
import com.jfireframework.dson.serializer.BeanSerializer;
import com.jfireframework.dson.serializer.CollectionSerializer;
import com.jfireframework.dson.serializer.MapSerializer;
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
			propertySerializer = new MapPropertySerializer();
		}
		else if (Collection.class.isAssignableFrom(fieldType))
		{
			propertySerializer = new CollectionPropertySerializer();
		}
		else if (Iterator.class.isAssignableFrom(fieldType))
		{
			propertySerializer = new IteratorPropertySerializer();
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
		protected void outputPropertyValue(Object propertyValue, StringOutput output)
		{
			beanSerializer.serialize(propertyValue, output);
		}
		
	}
	
	class UnFinalBeanPropertySerializer extends AbstractPropertySerializer
	{
		
		@Override
		protected void outputPropertyValue(Object propertyValue, StringOutput output)
		{
			jsonProcessor.serialize(propertyValue, output);
		}
		
	}
	
	class MapPropertySerializer extends AbstractPropertySerializer
	{
		private MapSerializer mapSerializer;
		
		public void initialize(Class<?> type, String property)
		{
			super.initialize(type, property);
			try
			{
				mapSerializer = jsonProcessor.mapSerializerClass().newInstance();
				mapSerializer.initialize(jsonProcessor, field.getType());
			}
			catch (Exception e)
			{
				throw new JustThrowException(e);
			}
		}
		
		@Override
		protected void outputPropertyValue(Object propertyValue, StringOutput output)
		{
			mapSerializer.serialize(propertyValue, output);
		}
	}
	
	class CollectionPropertySerializer extends AbstractPropertySerializer
	{
		private CollectionSerializer collectionSerializer;
		
		public void initialize(Class<?> type, String property)
		{
			super.initialize(type, property);
			try
			{
				collectionSerializer = jsonProcessor.collectionSerializerClass().newInstance();
				collectionSerializer.initialize(jsonProcessor, field.getType());
			}
			catch (Exception e)
			{
				throw new JustThrowException(e);
			}
		}
		
		@Override
		protected void outputPropertyValue(Object propertyValue, StringOutput output)
		{
			collectionSerializer.serialize(propertyValue, output);
		}
		
	}
	
	class IteratorPropertySerializer extends AbstractPropertySerializer
	{
		
		@Override
		protected void outputPropertyValue(Object propertyValue, StringOutput output)
		{
			output.append('{');
			Iterator<?> iterator = (Iterator<?>) propertyValue;
			int originLength = output.length();
			while (iterator.hasNext())
			{
				int length = output.length();
				jsonProcessor.serialize(iterator.next(), output);
				if (length != output.length())
				{
					output.append(',');
				}
			}
			if (originLength != output.length())
			{
				output.deleteLast();
			}
			output.append('}');
		}
	}
	
	class ArrayPropertySerializer extends AbstractPropertySerializer
	{
		private ArraySerializer arraySerializer;
		
		public void initialize(Class<?> type, String property)
		{
			super.initialize(type, property);
			try
			{
				arraySerializer = jsonProcessor.arraySerializerClass().newInstance();
				arraySerializer.initialize(jsonProcessor, field.getType());
			}
			catch (Exception e)
			{
				throw new JustThrowException(e);
			}
		}
		
		@Override
		protected void outputPropertyValue(Object propertyValue, StringOutput output)
		{
			arraySerializer.serialize(propertyValue, output);
		}
		
	}
}
