package com.jfireframework.dson.serializer;

import java.util.Map.Entry;
import com.jfireframework.dson.util.StringOutput;

public interface MapSerializer extends Serializer
{
	boolean serialize(Entry<?, ?> entry, StringOutput output);
}
