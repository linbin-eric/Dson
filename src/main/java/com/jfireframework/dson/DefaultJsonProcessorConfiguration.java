package com.jfireframework.dson;

import com.jfireframework.dson.metadata.EntryDeserializerFactory;
import com.jfireframework.dson.metadata.PropertySerializerFactory;
import com.jfireframework.dson.serializer.ArraySerializer;
import com.jfireframework.dson.serializer.BeanSerializer;
import com.jfireframework.dson.serializer.CollectionSerializer;
import com.jfireframework.dson.serializer.MapSerializer;
import com.jfireframework.dson.serializer.impl.ArraySerializerImpl;
import com.jfireframework.dson.serializer.impl.ReflectBeanSerializeDescriptor;
import com.jfireframework.dson.serializer.impl.CollectionSerizlizerImpl;
import com.jfireframework.dson.serializer.impl.MapSerializeDescriptor;
import com.jfireframework.dson.serializer.property.ReflectPropertySerializerFactory;

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
		return MapSerializeDescriptor.class;
	}
	
	@Override
	public Class<? extends CollectionSerializer> getCollectionSerializerClass()
	{
		return CollectionSerizlizerImpl.class;
	}
	
	@Override
	public Class<? extends BeanSerializer> getBeanSerializerClass()
	{
		return ReflectBeanSerializeDescriptor.class;
	}
	
	@Override
	public Class<? extends ArraySerializer> getArraySerializerClass()
	{
		return ArraySerializerImpl.class;
	}

	@Override
	public Class<? extends EntryDeserializerFactory> getEntryDeserializerFactoryClass()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
