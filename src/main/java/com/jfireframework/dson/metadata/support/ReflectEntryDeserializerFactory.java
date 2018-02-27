package com.jfireframework.dson.metadata.support;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import com.jfireframework.baseutil.encrypt.EnDecrpt;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.dson.deserializer.DeserializeDescriber;
import com.jfireframework.dson.deserializer.Deserializer;
import com.jfireframework.dson.deserializer.PropertyDeserializer;
import com.jfireframework.dson.metadata.EntryDeserializerFactory;
import com.jfireframework.dson.metadata.json.DsonObject;
import com.jfireframework.dson.metadata.json.Entry;
import com.jfireframework.dson.metadata.json.JsonValueType;

public class ReflectEntryDeserializerFactory implements EntryDeserializerFactory
{
	@Override
	public PropertyDeserializer[] get(Class<?> type, Deserializer deserializer)
	{
		List<PropertyDeserializer> list = new ArrayList<PropertyDeserializer>();
		for (Field field : ReflectUtil.getAllFields(type))
		{
			Class<?> fieldType = field.getType();
			AbstractEntryDeserializer entryDeserializer = null;
			if (fieldType == int.class || fieldType == Integer.class)
			{
				entryDeserializer = new IntegerEntryDeserializer();
			}
			else if (fieldType == short.class || fieldType == Short.class)
			{
				entryDeserializer = new ShortEntryDeserializer();
			}
			else if (fieldType == long.class || fieldType == Long.class)
			{
				entryDeserializer = new LongDeserializer();
			}
			else if (fieldType == byte.class || fieldType == Byte.class)
			{
				entryDeserializer = new ByteDeserializer();
			}
			else if (fieldType == float.class || fieldType == Float.class)
			{
				entryDeserializer = new FloatDeserializer();
			}
			else if (fieldType == double.class || fieldType == Double.class)
			{
				entryDeserializer = new DoubleDeseriablizer();
			}
			else if (fieldType == boolean.class || fieldType == Boolean.class)
			{
				entryDeserializer = new BooleanDeserializer();
			}
			else if (fieldType == char.class || fieldType == Character.class)
			{
				entryDeserializer = new CharDeserializer();
			}
			else if (fieldType == String.class)
			{
				entryDeserializer = new StringDeserializer();
			}
			entryDeserializer.initialize(deserializer, field, field.getName());
		}
		return list.toArray(new PropertyDeserializer[list.size()]);
	}
	
	abstract class AbstractEntryDeserializer implements PropertyDeserializer
	{
		protected String		propertyName;
		protected Field			field;
		protected Deserializer	deserializer;
		
		public void initialize(Deserializer deserializer, Field field, String propertyName)
		{
			this.deserializer = deserializer;
			this.propertyName = propertyName;
			this.field = field;
			field.setAccessible(true);
			
		}
		
		@Override
		public String propertyName()
		{
			return propertyName;
		}
		
	}
	
	class IntegerEntryDeserializer extends AbstractEntryDeserializer
	{
		
		@Override
		public void deserialize(Entry entry, Object bean)
		{
			if (entry.getValueType() != JsonValueType.NUMBER_LONG)
			{
				return;
			}
			try
			{
				field.set(bean, ((Long) entry.getValue()).intValue());
			}
			catch (Exception e)
			{
				throw new JustThrowException(e);
			}
		}
	}
	
	class ShortEntryDeserializer extends AbstractEntryDeserializer
	{
		@Override
		public void deserialize(Entry entry, Object bean)
		{
			if (entry.getValueType() != JsonValueType.NUMBER_LONG)
			{
				return;
			}
			try
			{
				field.set(bean, ((Long) entry.getValue()).shortValue());
			}
			catch (Exception e)
			{
				throw new JustThrowException(e);
			}
		}
		
	}
	
	class LongDeserializer extends AbstractEntryDeserializer
	{
		
		@Override
		public void deserialize(Entry entry, Object bean)
		{
			if (entry.getValueType() != JsonValueType.NUMBER_LONG)
			{
				return;
			}
			try
			{
				field.set(bean, ((Long) entry.getValue()));
			}
			catch (Exception e)
			{
				throw new JustThrowException(e);
			}
		}
	}
	
	class ByteDeserializer extends AbstractEntryDeserializer
	{
		
		@Override
		public void deserialize(Entry entry, Object bean)
		{
			if (entry.getValueType() != JsonValueType.NUMBER_LONG)
			{
				return;
			}
			try
			{
				field.set(bean, ((Long) entry.getValue()).byteValue());
			}
			catch (Exception e)
			{
				throw new JustThrowException(e);
			}
		}
	}
	
	class FloatDeserializer extends AbstractEntryDeserializer
	{
		
		@Override
		public void deserialize(Entry entry, Object bean)
		{
			if (entry.getValueType() != JsonValueType.NUMBER_DOUBLE)
			{
				return;
			}
			try
			{
				field.set(bean, ((Double) entry.getValue()).floatValue());
			}
			catch (Exception e)
			{
				throw new JustThrowException(e);
			}
		}
	}
	
	class DoubleDeseriablizer extends AbstractEntryDeserializer
	{
		
		@Override
		public void deserialize(Entry entry, Object bean)
		{
			if (entry.getValueType() != JsonValueType.NUMBER_DOUBLE)
			{
				return;
			}
			try
			{
				field.set(bean, ((Double) entry.getValue()));
			}
			catch (Exception e)
			{
				throw new JustThrowException(e);
			}
		}
		
	}
	
	class BooleanDeserializer extends AbstractEntryDeserializer
	{
		
		@Override
		public void deserialize(Entry entry, Object bean)
		{
			if (entry.getValueType() != JsonValueType.BOOLEAN)
			{
				return;
			}
			try
			{
				field.set(bean, ((Boolean) entry.getValue()));
			}
			catch (Exception e)
			{
				throw new JustThrowException(e);
			}
		}
	}
	
	class CharDeserializer extends AbstractEntryDeserializer
	{
		
		@Override
		public void deserialize(Entry entry, Object bean)
		{
			if (entry.getValueType() != JsonValueType.STRING)
			{
				return;
			}
			try
			{
				field.set(bean, ((String) entry.getValue()).charAt(0));
			}
			catch (Exception e)
			{
				throw new JustThrowException(e);
			}
		}
	}
	
	class StringDeserializer extends AbstractEntryDeserializer
	{
		
		@Override
		public void deserialize(Entry entry, Object bean)
		{
			if (entry.getValueType() != JsonValueType.STRING)
			{
				return;
			}
			try
			{
				field.set(bean, ((String) entry.getValue()));
			}
			catch (Exception e)
			{
				throw new JustThrowException(e);
			}
		}
		
	}
	
	class BeanDeserializer extends AbstractEntryDeserializer
	{
		private DeserializeDescriber describer;
		
		@Override
		public void initialize(Deserializer deserializer, Field field, String propertyName)
		{
			super.initialize(deserializer, field, propertyName);
			describer = deserializer.describe(field.getType());
		}
		
		@Override
		public void deserialize(Entry entry, Object bean)
		{
			if (entry.getValueType() != JsonValueType.COLLECTION)
			{
				return;
			}
			Object instance = describer.deserialize((DsonObject) entry.getValue());
			try
			{
				field.set(bean, instance);
			}
			catch (Exception e)
			{
				throw new JustThrowException(e);
			}
		}
		
	}
	
}
