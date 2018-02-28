package com.jfireframework.dson.deserializer.support;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.jfireframework.dson.deserializer.DeserializeDescriptor;
import com.jfireframework.dson.deserializer.Deserializer;
import com.jfireframework.dson.deserializer.buildin.BooleanDeserializeDescriptor;
import com.jfireframework.dson.deserializer.buildin.ByteDeserializeDescriptor;
import com.jfireframework.dson.deserializer.buildin.CharacterDeserializeDescriptor;
import com.jfireframework.dson.deserializer.buildin.DoubleDeserializeDescriptor;
import com.jfireframework.dson.deserializer.buildin.FloatDeserializeDescriptor;
import com.jfireframework.dson.deserializer.buildin.IntegerDeserializeDescriptor;
import com.jfireframework.dson.deserializer.buildin.LongDeserializeDescriptor;
import com.jfireframework.dson.deserializer.buildin.ObjectDeserializeDescriptor;
import com.jfireframework.dson.deserializer.buildin.ShortDeserializeDescriptor;
import com.jfireframework.dson.deserializer.buildin.StringDeserializDescriptor;
import com.jfireframework.dson.deserializer.buildin.array.BooleanArrayDeserializeDescriptor;
import com.jfireframework.dson.deserializer.buildin.array.ByteArrayDeserializeDescriptor;
import com.jfireframework.dson.deserializer.buildin.array.CharArrayDeserializeDescriptor;
import com.jfireframework.dson.deserializer.buildin.array.DoubleArrayDeserializeDescriptor;
import com.jfireframework.dson.deserializer.buildin.array.FloatArrayDeserializeDescriptor;
import com.jfireframework.dson.deserializer.buildin.array.IntArrayDeserializeDescriptor;
import com.jfireframework.dson.deserializer.buildin.array.LongArrayDeserializeDescriptor;
import com.jfireframework.dson.deserializer.buildin.array.ShortArrayDeserializeDescriptor;
import com.jfireframework.dson.deserializer.buildin.array.StringArrayDeserializeDescriptor;
import com.jfireframework.dson.deserializer.impl.ArrayDeserializeDescriptor;
import com.jfireframework.dson.deserializer.impl.CollectionDeserializeDecriptor;
import com.jfireframework.dson.deserializer.impl.MapDeserializeDescriptor;
import com.jfireframework.dson.deserializer.impl.ReflectBeanDeserializeDescriptor;

public class DefaultDeserializer implements Deserializer
{
	private ConcurrentMap<Type, DeserializeDescriptor> store = new ConcurrentHashMap<Type, DeserializeDescriptor>();
	
	public DefaultDeserializer()
	{
		store.put(Character.class, new CharacterDeserializeDescriptor());
		store.put(Byte.class, new ByteDeserializeDescriptor());
		store.put(Integer.class, new IntegerDeserializeDescriptor());
		store.put(Short.class, new ShortDeserializeDescriptor());
		store.put(Long.class, new LongDeserializeDescriptor());
		store.put(Float.class, new FloatDeserializeDescriptor());
		store.put(Double.class, new DoubleDeserializeDescriptor());
		store.put(Boolean.class, new BooleanDeserializeDescriptor());
		store.put(Object.class, new ObjectDeserializeDescriptor());
		store.put(String.class, new StringDeserializDescriptor());
		//
		store.put(boolean[].class, new BooleanArrayDeserializeDescriptor());
		store.put(byte[].class, new ByteArrayDeserializeDescriptor());
		store.put(char[].class, new CharArrayDeserializeDescriptor());
		store.put(double[].class, new DoubleArrayDeserializeDescriptor());
		store.put(float[].class, new FloatArrayDeserializeDescriptor());
		store.put(int[].class, new IntArrayDeserializeDescriptor());
		store.put(long[].class, new LongArrayDeserializeDescriptor());
		store.put(short[].class, new ShortArrayDeserializeDescriptor());
		store.put(String[].class, new StringArrayDeserializeDescriptor());
	}
	
	@Override
	public DeserializeDescriptor describe(Type type)
	{
		DeserializeDescriptor describer = store.get(type);
		if (describer == null)
		{
			if (type instanceof ParameterizedType)
			{
				Class<?> rawType = (Class<?>) ((ParameterizedType) type).getRawType();
				if (Map.class.isAssignableFrom(rawType))
				{
					describer = new MapDeserializeDescriptor();
				}
				else if (Collection.class.isAssignableFrom(rawType))
				{
					describer = new CollectionDeserializeDecriptor();
				}
				else
				{
					describer = new ReflectBeanDeserializeDescriptor();
				}
				describer.initialize(type, this);
				store.put(type, describer);
			}
			else if (type instanceof Class<?>)
			{
				if (Map.class.isAssignableFrom((Class<?>) type))
				{
					describer = new MapDeserializeDescriptor();
				}
				else if (Collection.class.isAssignableFrom((Class<?>) type))
				{
					describer = new CollectionDeserializeDecriptor();
				}
				else if (((Class<?>) type).isArray())
				{
					describer = new ArrayDeserializeDescriptor();
				}
				else
				{
					describer = new ReflectBeanDeserializeDescriptor();
				}
				describer.initialize(type, this);
				store.put(type, describer);
			}
			else
			{
				throw new IllegalArgumentException();
			}
		}
		return describer;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(Type type, String json)
	{
		DeserializeDescriptor deserializeDescriber = describe(type);
		return (T) deserializeDescriber.deserialize(json);
	}
	
}
