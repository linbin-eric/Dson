package com.jfireframework.dson.serializer.buildin;

import java.lang.reflect.Type;
import com.jfireframework.dson.serializer.SerializeDescriptor;
import com.jfireframework.dson.serializer.Serializer;
import com.jfireframework.dson.util.StringOutput;

public class CharacterSerializeDescriptor implements SerializeDescriptor
{
	@Override
	public boolean serialize(Object entity, StringOutput output)
	{
		if (entity == null)
		{
			return false;
		}
		output.appendDoubleQuotes().append((Character) entity).appendDoubleQuotes();
		return true;
	}
	
	@Override
	public boolean serializeWithoutDoubleQuotes(Object entity, StringOutput output)
	{
		if (entity == null)
		{
			return false;
		}
		output.append((Character) entity);
		return true;
	}
	
	@Override
	public void initialize(Serializer serializer, Type type)
	{
		// TODO Auto-generated method stub
		
	}
	
}
