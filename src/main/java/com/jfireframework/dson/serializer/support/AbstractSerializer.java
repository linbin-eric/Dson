package com.jfireframework.dson.serializer.support;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import com.jfireframework.dson.serializer.SerializeDescriptor;
import com.jfireframework.dson.serializer.Serializer;

public abstract class AbstractSerializer implements Serializer
{
	protected List<SerializeDescriptor> registry = new CopyOnWriteArrayList<SerializeDescriptor>();
	
	@Override
	public int registerSerializeDescriptor(SerializeDescriptor serializeDescriptor)
	{
		registry.add(serializeDescriptor);
		return registry.indexOf(serializeDescriptor);
	}
	
	@Override
	public SerializeDescriptor get(int index)
	{
		return registry.get(index);
	}
}
