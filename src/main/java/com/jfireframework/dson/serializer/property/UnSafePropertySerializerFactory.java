package com.jfireframework.dson.serializer.property;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.dson.JsonProcessor;
import com.jfireframework.dson.metadata.PropertySerializerFactory;
import com.jfireframework.dson.serializer.PropertySerializer;
import com.jfireframework.dson.util.StringOutput;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class UnSafePropertySerializerFactory implements PropertySerializerFactory
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
			propertySerializer = new StringPropertySerializer();
		}
		else if (fieldType == boolean.class || fieldType == Boolean.class)
		{
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
		}
		else
		{
		}
		propertySerializer.initialize(type, property);
		return propertySerializer;
	}
	
	static abstract class AbstractUnsafePropertySerializer implements PropertySerializer
	{
		protected static final Unsafe	unsafe	= ReflectUtil.getUnsafe();
		protected long					offset;
		protected String				propertyName;
		
		@Override
		public void initialize(Class<?> type, String property)
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
			propertyName = field.getName();
			offset = unsafe.objectFieldOffset(field);
		}
	}
	
	class NumberPropertySerializer extends AbstractUnsafePropertySerializer
	{
		
		@Override
		public boolean serialize(Object host, StringOutput output)
		{
			Number value = (Number) unsafe.getObject(host, offset);
			if (value == null)
			{
				return false;
			}
			output.append('"').append(propertyName).append("\":").append(value);
			return true;
		}
	}
	
	class StringPropertySerializer extends AbstractUnsafePropertySerializer
	{
		@Override
		public boolean serialize(Object host, StringOutput output)
		{
			String value = (String) unsafe.getObject(host, offset);
			if (value == null)
			{
				return false;
			}
			output.append('"').append(propertyName).append("\":\"").append(value).append('"');
			return true;
		}
	}
}
