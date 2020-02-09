package com.jfireframework.dson.serializer.support;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import com.jfireframework.dson.serializer.TypeWriter;
import com.jfireframework.dson.serializer.JsonWriter;

public abstract class AbstractSerializer implements JsonWriter
{
	protected List<TypeWriter> registry = new CopyOnWriteArrayList<TypeWriter>();
	
	@Override
	public int registerSerializeDescriptor(TypeWriter serializeDescriptor)
	{
		registry.add(serializeDescriptor);
		return registry.indexOf(serializeDescriptor);
	}
	
	@Override
	public TypeWriter get(int index)
	{
		return registry.get(index);
	}
}
