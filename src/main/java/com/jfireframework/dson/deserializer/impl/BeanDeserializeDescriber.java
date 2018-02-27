package com.jfireframework.dson.deserializer.impl;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.dson.deserializer.DeserializeDescriber;
import com.jfireframework.dson.deserializer.Deserializer;
import com.jfireframework.dson.deserializer.PropertyDeserializer;
import com.jfireframework.dson.deserializer.token.Lexer;
import com.jfireframework.dson.metadata.EntryDeserializerFactory;
import com.jfireframework.dson.metadata.json.DsonObject;
import com.jfireframework.dson.metadata.json.Entry;
import com.jfireframework.dson.metadata.json.JsonCollection;

public class BeanDeserializeDescriber implements DeserializeDescriber
{
	private Map<String, PropertyDeserializer>	store	= new HashMap<String, PropertyDeserializer>();
	private Class<?>							type;
	
	@Override
	public Object deserialize(DsonObject dsonObject)
	{
		JsonCollection collection = (JsonCollection) dsonObject;
		List<Entry> entries = collection.getEntries();
		try
		{
			Object instance = type.newInstance();
			for (Entry entry : entries)
			{
				PropertyDeserializer entryDeserializer = store.get(entry.getName());
				if (entryDeserializer != null)
				{
					entryDeserializer.deserialize(entry, instance);
				}
			}
			return instance;
		}
		catch (Exception e)
		{
			throw new JustThrowException(e);
		}
	}
	
	@Override
	public void initialize(Type type, Deserializer deserializer)
	{
		this.type = (Class<?>) type;
		EntryDeserializerFactory entryDeserializerFactory = deserializer.entryDeserializerFactory();
		PropertyDeserializer[] entryDeserializers = entryDeserializerFactory.get(this.type, deserializer);
		for (PropertyDeserializer entryDeserializer : entryDeserializers)
		{
			store.put(entryDeserializer.propertyName(), entryDeserializer);
		}
	}
	
	@Override
	public Object deserialize(String json)
	{
		return deserialize(new Lexer(json).parse());
	}
	
}
