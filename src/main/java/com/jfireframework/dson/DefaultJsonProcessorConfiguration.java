package com.jfireframework.dson;

import com.jfireframework.dson.metadata.PropertySerializerFactory;
import com.jfireframework.dson.metadata.ReflectPropertySerializerFactory;
import com.jfireframework.dson.serializer.ArraySerializer;
import com.jfireframework.dson.serializer.BeanSerializer;
import com.jfireframework.dson.serializer.CollectionSerializer;
import com.jfireframework.dson.serializer.MapSerializer;
import com.jfireframework.dson.serializer.impl.ArraySerializerImpl;
import com.jfireframework.dson.serializer.impl.BeanSerializerImpl;
import com.jfireframework.dson.serializer.impl.CollectionSerizlizerImpl;
import com.jfireframework.dson.serializer.impl.MapSerializerImpl;

public class DefaultJsonProcessorConfiguration implements JsonProcessorConfiguration
{
	
	@Override
	public Class<? extends PropertySerializerFactory> getPropertySerializerFactoryClass()
	{
		return ReflectPropertySerializerFactory.class;
	}
	
	@Override
	public Class<? extends MapSerializer> getMapSerializerClass()
	{
		return MapSerializerImpl.class;
	}
	
	@Override
	public Class<? extends CollectionSerializer> getCollectionSerializerClass()
	{
		return CollectionSerizlizerImpl.class;
	}
	
	@Override
	public Class<? extends BeanSerializer> getBeanSerializerClass()
	{
		return BeanSerializerImpl.class;
	}
	
	@Override
	public Class<? extends ArraySerializer> getArraySerializerClass()
	{
		return ArraySerializerImpl.class;
	}
	
}
