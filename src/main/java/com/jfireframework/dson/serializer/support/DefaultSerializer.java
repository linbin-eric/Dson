package com.jfireframework.dson.serializer.support;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.jfireframework.dson.serializer.SerializeDescriptor;
import com.jfireframework.dson.serializer.Serializer;
import com.jfireframework.dson.serializer.buildin.BooleanSerializeDescriptor;
import com.jfireframework.dson.serializer.buildin.ByteSerializeDescriptor;
import com.jfireframework.dson.serializer.buildin.CharacterSerializeDescriptor;
import com.jfireframework.dson.serializer.buildin.DateSerializeDescriptor;
import com.jfireframework.dson.serializer.buildin.DoubleSerializeDescriptor;
import com.jfireframework.dson.serializer.buildin.FloatSerializeDescriptor;
import com.jfireframework.dson.serializer.buildin.IntegerSerializeDescriptor;
import com.jfireframework.dson.serializer.buildin.LongSerializeDescriptor;
import com.jfireframework.dson.serializer.buildin.ShortSerializeDescriptor;
import com.jfireframework.dson.serializer.buildin.StringSerializeDescriptor;
import com.jfireframework.dson.serializer.impl.ArraySerializeDescriptor;
import com.jfireframework.dson.serializer.impl.CollectionSerializeDescriptor;
import com.jfireframework.dson.serializer.impl.EnumSerializeDescriptor;
import com.jfireframework.dson.serializer.impl.MapSerializeDescriptor;
import com.jfireframework.dson.serializer.impl.ReflectBeanSerializeDescriptor;
import com.jfireframework.dson.util.StringOutput;

public class DefaultSerializer implements Serializer
{
	private ConcurrentHashMap<Type, SerializeDescriptor> store = new ConcurrentHashMap<Type, SerializeDescriptor>();
	
	public DefaultSerializer()
	{
		store.put(Integer.class, new IntegerSerializeDescriptor());
		store.put(Short.class, new ShortSerializeDescriptor());
		store.put(Long.class, new LongSerializeDescriptor());
		store.put(Float.class, new FloatSerializeDescriptor());
		store.put(Double.class, new DoubleSerializeDescriptor());
		store.put(Byte.class, new ByteSerializeDescriptor());
		store.put(Boolean.class, new BooleanSerializeDescriptor());
		store.put(Character.class, new CharacterSerializeDescriptor());
		store.put(String.class, new StringSerializeDescriptor());
		store.put(Date.class, new DateSerializeDescriptor());
		store.put(java.sql.Date.class, new DateSerializeDescriptor());
	}
	
	@Override
	public SerializeDescriptor describe(Type type)
	{
		SerializeDescriptor serializeDescriptor = store.get(type);
		if (serializeDescriptor == null)
		{
			if (type instanceof ParameterizedType)
			{
				Class<?> rawType = (Class<?>) ((ParameterizedType) type).getRawType();
				if (Map.class.isAssignableFrom(rawType))
				{
					serializeDescriptor = new MapSerializeDescriptor();
				}
				else if (Collection.class.isAssignableFrom(rawType))
				{
					serializeDescriptor = new CollectionSerializeDescriptor();
				}
				else if (Enum.class.isAssignableFrom(rawType))
				{
					serializeDescriptor = new EnumSerializeDescriptor();
				}
				else
				{
					serializeDescriptor = new ReflectBeanSerializeDescriptor();
				}
			}
			else if (type instanceof Class<?>)
			{
				if (Map.class.isAssignableFrom((Class<?>) type))
				{
					serializeDescriptor = new MapSerializeDescriptor();
				}
				else if (Collection.class.isAssignableFrom((Class<?>) type))
				{
					serializeDescriptor = new CollectionSerializeDescriptor();
				}
				else if (Enum.class.isAssignableFrom((Class<?>) type))
				{
					serializeDescriptor = new EnumSerializeDescriptor();
				}
				else if (((Class<?>) type).isArray())
				{
					serializeDescriptor = new ArraySerializeDescriptor();
				}
				else
				{
					serializeDescriptor = new ReflectBeanSerializeDescriptor();
				}
			}
			else if (type instanceof GenericArrayType)
			{
				serializeDescriptor = new ArraySerializeDescriptor();
			}
			else
			{
				throw new IllegalArgumentException("当前类型:" + type);
			}
			store.putIfAbsent(type, serializeDescriptor);
			serializeDescriptor.initialize(this, type);
		}
		return serializeDescriptor;
	}
	
	@Override
	public void serialize(Object entity, StringOutput output)
	{
		if (entity == null)
		{
			return;
		}
		SerializeDescriptor serializeDescriptor = describe(entity.getClass());
		serializeDescriptor.serialize(entity, output);
	}
	
	@Override
	public void serializeWithoutDoubleQuotes(Object entity, StringOutput output)
	{
		if (entity == null)
		{
			return;
		}
		SerializeDescriptor serializeDescriptor = describe(entity.getClass());
		serializeDescriptor.serializeWithoutDoubleQuotes(entity, output);
	}
	
}
