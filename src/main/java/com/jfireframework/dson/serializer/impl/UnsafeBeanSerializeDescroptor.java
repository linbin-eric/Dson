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
import com.jfireframework.dson.strategy.SerializeDefinition;
import com.jfireframework.dson.util.StringOutput;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class UnsafeBeanSerializeDescroptor implements SerializeDescriptor
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
		void serialize(Object host, StringOutput output);
	}
	
	private PropertySerializer[]	propertySerializers;
	private Serializer				serializer;
	private final Unsafe			unsafe	= ReflectUtil.getUnsafe();
	
	@Override
	public void serializeWithoutDoubleQuotes(Object entity, StringOutput output)
	{
		serialize(entity, output);
	}
	
	@Override
	public void serialize(Object entity, StringOutput output)
	{
		if (propertySerializers.length == 0 || entity == null)
		{
			return;
		}
		output.append('{');
		int length = output.length();
		for (PropertySerializer each : propertySerializers)
		{
			each.serialize(entity, output);
		}
		if (length != output.length())
		{
			output.deleteLast();
		}
		output.append('}');
	}
	
	@Override
	public void initialize(Serializer serializer, Type type, Map<Type, SerializeDescriptor> map)
	{
		this.serializer = serializer;
		List<PropertySerializer> list = new ArrayList<PropertySerializer>();
		for (Field field : ReflectUtil.getAllFields((Class<?>) type))
		{
			if (field.getName().contains("this") || Modifier.isStatic(field.getModifiers()))
			{
				continue;
			}
			Class<?> fieldType = field.getType();
			AbstractPropertySerializer propertySerializer;
			if (field.isAnnotationPresent(SerializeDefinition.class))
			{
				propertySerializer = new UserDefinitionSerializer();
			}
			else if (fieldType == int.class //
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
			else if (fieldType == String.class)
			{
				propertySerializer = new StringProeprtySerializer();
			}
			else if (fieldType == Character.class || fieldType == char.class)
			{
				propertySerializer = new CharacterPropertySerializer();
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
			else if (Enum.class.isAssignableFrom(fieldType))
			{
				propertySerializer = new EnumPropertySerializer();
			}
			else if (Modifier.isFinal(fieldType.getModifiers()))
			{
				propertySerializer = new FinalBeanPropertySerializer();
			}
			else
			{
				propertySerializer = new UnFinalBeanPropertySerializer();
			}
			propertySerializer.initialize(field, map);
			list.add(propertySerializer);
		}
		this.propertySerializers = list.toArray(new PropertySerializer[list.size()]);
	}
	
	abstract class AbstractPropertySerializer implements PropertySerializer
	{
		protected String	propertyName;
		protected long		offset;
		
		public void initialize(Field field, Map<Type, SerializeDescriptor> map)
		{
			propertyName = field.getName();
			offset = unsafe.objectFieldOffset(field);
		}
		
		@Override
		public void serialize(Object entity, StringOutput output)
		{
			try
			{
				Object propertyValue = unsafe.getObject(entity, offset);
				if (propertyValue == null)
				{
					return;
				}
				output.appendDoubleQuotes().append(propertyName).appendDoubleQuotes().append(':');
				outputPropertyValue(propertyValue, output);
				output.append(',');
			}
			catch (Exception e)
			{
				throw new JustThrowException(e);
			}
		}
		
		protected void outputPropertyValue(Object propertyValue, StringOutput output)
		{
			throw new UnsupportedOperationException();
		}
	}
	
	class IntPropertySerializer extends AbstractPropertySerializer
	{
		
		@Override
		public void serialize(Object entity, StringOutput output)
		{
			try
			{
				int propertyValue = unsafe.getInt(entity, offset);
				output.appendDoubleQuotes().append(propertyName).appendDoubleQuotes().append(':').append(propertyValue);
				output.append(',');
			}
			catch (Exception e)
			{
				throw new JustThrowException(e);
			}
		}
		
	}
	class ShortPropertySerializer extends AbstractPropertySerializer
	{
		
		@Override
		public void serialize(Object entity, StringOutput output)
		{
			try
			{
				short propertyValue = unsafe.getShort(entity, offset);
				output.appendDoubleQuotes().append(propertyName).appendDoubleQuotes().append(':').append(propertyValue);
				output.append(',');
			}
			catch (Exception e)
			{
				throw new JustThrowException(e);
			}
		}
		
	}
	
	class NumberPropertySerializer extends AbstractPropertySerializer
	{
		
		@Override
		protected void outputPropertyValue(Object propertyValue, StringOutput output)
		{
			output.append(propertyValue);
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
			output.append(propertyValue);
		}
		
	}
	
	class CharacterPropertySerializer extends AbstractPropertySerializer
	{
		
		@Override
		protected void outputPropertyValue(Object propertyValue, StringOutput output)
		{
			output.appendDoubleQuotes().append(propertyValue).appendDoubleQuotes();
		}
		
	}
	
	class EnumPropertySerializer extends AbstractPropertySerializer
	{
		
		@Override
		protected void outputPropertyValue(Object propertyValue, StringOutput output)
		{
			Enum<?> instance = (Enum<?>) propertyValue;
			output.appendDoubleQuotes().append(instance.name()).appendDoubleQuotes();
		}
		
	}
	
	class FinalBeanPropertySerializer extends AbstractPropertySerializer
	{
		private SerializeDescriptor serializeDescriptor;
		
		@Override
		public void initialize(Field field, Map<Type, SerializeDescriptor> map)
		{
			super.initialize(field, map);
			serializeDescriptor = serializer.describe(field.getType(), map);
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
		public void initialize(Field field, Map<Type, SerializeDescriptor> map)
		{
			super.initialize(field, map);
			serializeDescriptor = serializer.describe(field.getGenericType(), map);
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
		public void initialize(Field field, Map<Type, SerializeDescriptor> map)
		{
			super.initialize(field, map);
			serializeDescriptor = serializer.describe(field.getGenericType(), map);
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
		public void initialize(Field field, Map<Type, SerializeDescriptor> map)
		{
			super.initialize(field, map);
			serializeDescriptor = serializer.describe(field.getGenericType(), map);
		}
		
		@Override
		protected void outputPropertyValue(Object propertyValue, StringOutput output)
		{
			serializeDescriptor.serialize(propertyValue, output);
		}
		
	}
	
	class UserDefinitionSerializer extends AbstractPropertySerializer
	{
		private SerializeDescriptor serializeDescriptor;
		
		@Override
		public void initialize(Field field, Map<Type, SerializeDescriptor> map)
		{
			super.initialize(field, map);
			Class<? extends SerializeDescriptor> ckass = field.getAnnotation(SerializeDefinition.class).value();
			try
			{
				serializeDescriptor = ckass.newInstance();
				serializeDescriptor.initialize(serializer, field.getGenericType(), map);
			}
			catch (Exception e)
			{
				throw new JustThrowException(e);
			}
		}
		
		@Override
		protected void outputPropertyValue(Object propertyValue, StringOutput output)
		{
			serializeDescriptor.serialize(propertyValue, output);
		}
		
	}
}
