package com.jfireframework.dson.deserializer;

import java.lang.reflect.Type;
import com.jfireframework.dson.metadata.json.DsonObject;

public interface DeserializeDescriber
{
	void initialize(Type type, Deserializer deserializer);
	
	Object deserialize(DsonObject dsonObject);
	
	Object deserialize(String json);
}
