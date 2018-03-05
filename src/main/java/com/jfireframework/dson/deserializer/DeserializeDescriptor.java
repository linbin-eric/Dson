package com.jfireframework.dson.deserializer;

import java.lang.reflect.Type;
import java.util.Map;
import com.jfireframework.dson.metadata.json.DsonObject;
import com.jfireframework.dson.metadata.json.Element;
import com.jfireframework.dson.metadata.json.Entry;

public interface DeserializeDescriptor
{
	void initialize(Type type, Deserializer deserializer, Map<Type, DeserializeDescriptor> map);
	
	Object deserialize(DsonObject dsonObject);
	
	Object deserialize(String json);
	
	Object deserialize(Entry entry);
	
	Object deserialize(Element element);
	
}
