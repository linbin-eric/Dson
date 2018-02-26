package com.jfireframework.dson;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.dson.serializer.ArraySerializer;
import com.jfireframework.dson.serializer.BeanSerializer;
import com.jfireframework.dson.serializer.CollectionSerializer;
import com.jfireframework.dson.serializer.MapSerializer;
import com.jfireframework.dson.serializer.Serializer;
import com.jfireframework.dson.serializer.buildin.BooleanSerializer;
import com.jfireframework.dson.serializer.buildin.NumberSerializer;
import com.jfireframework.dson.serializer.buildin.StringSerializer;
import com.jfireframework.dson.serializer.property.PropertySerializerFactory;
import com.jfireframework.dson.util.StringOutput;

public class JsonProcessorImpl implements JsonProcessor
{
	private PropertySerializerFactory				propertySerializerFactory;
	private Class<? extends MapSerializer>			mapSerializerClass;
	private Class<? extends CollectionSerializer>	collectionSerializerClass;
	private Class<? extends BeanSerializer>			beanSerializerClass;
	private Class<? extends ArraySerializer>		arraySerializerClass;
	private ConcurrentMap<Class<?>, Serializer>		store	= new ConcurrentHashMap<Class<?>, Serializer>();
	
	public JsonProcessorImpl()
	{
		store.put(String.class, new StringSerializer());
		store.put(Integer.class, new NumberSerializer());
		store.put(Short.class, new NumberSerializer());
		store.put(Long.class, new NumberSerializer());
		store.put(Float.class, new NumberSerializer());
		store.put(Double.class, new NumberSerializer());
		store.put(Byte.class, new NumberSerializer());
		store.put(Boolean.class, new BooleanSerializer());
		store.put(int.class, new NumberSerializer());
		store.put(short.class, new NumberSerializer());
		store.put(long.class, new NumberSerializer());
		store.put(float.class, new NumberSerializer());
		store.put(double.class, new NumberSerializer());
		store.put(byte.class, new NumberSerializer());
		store.put(boolean.class, new BooleanSerializer());
	}
	
	@Override
	public PropertySerializerFactory propertySerializerFactory()
	{
		return propertySerializerFactory;
	}
	
	@Override
	public Class<? extends MapSerializer> mapSerializerClass()
	{
		return mapSerializerClass;
	}
	
	@Override
	public Class<? extends CollectionSerializer> collectionSerializerClass()
	{
		return collectionSerializerClass;
	}
	
	@Override
	public Class<? extends BeanSerializer> beanSerializerClass()
	{
		return beanSerializerClass;
	}
	
	@Override
	public Class<? extends ArraySerializer> arraySerializerClass()
	{
		return arraySerializerClass;
	}
	
	@Override
	public void serialize(Object entity, StringOutput output)
	{
		Class<?> type = entity.getClass();
		Serializer serializer = store.get(type);
		if (serializer == null)
		{
			if (Map.class.isAssignableFrom(type))
			{
				try
				{
					MapSerializer mapSerializer = mapSerializerClass.newInstance();
					mapSerializer.initialize(this, type);
					serializer = (serializer = store.putIfAbsent(type, mapSerializer)) == null ? mapSerializer : serializer;
				}
				catch (Exception e)
				{
					throw new JustThrowException(e);
				}
			}
			else if (Collection.class.isAssignableFrom(type))
			{
				
			}
			else if (type.isArray())
			{
				
			}
			else
			{
				try
				{
					BeanSerializer beanSerializer = beanSerializerClass.newInstance();
					beanSerializer.initialize(this, type);
					serializer = (serializer = store.putIfAbsent(type, beanSerializer)) == null ? beanSerializer : serializer;
				}
				catch (Throwable e)
				{
					throw new JustThrowException(e);
				}
			}
		}
		serializer.serialize(entity, output);
	}
	
	@Override
	public void initialize(JsonProcessorConfiguration configuration)
	{
		beanSerializerClass = configuration.getBeanSerializerClass();
		mapSerializerClass = configuration.getMapSerializerClass();
		collectionSerializerClass = configuration.getCollectionSerializerClass();
		arraySerializerClass = configuration.getArraySerializerClass();
		try
		{
			propertySerializerFactory = configuration.getPropertySerializerFactoryClass().newInstance();
		}
		catch (Exception e)
		{
			throw new JustThrowException(e);
		}
	}
	
}
