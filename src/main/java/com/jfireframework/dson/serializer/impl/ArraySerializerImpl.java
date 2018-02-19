package com.jfireframework.dson.serializer.impl;

import java.lang.reflect.Modifier;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.dson.JsonProcessor;
import com.jfireframework.dson.StringOutput;
import com.jfireframework.dson.serializer.ArraySerializer;
import com.jfireframework.dson.serializer.BeanSerializer;

public class ArraySerializerImpl implements ArraySerializer
{
	private JsonProcessor	jsonProcessor;
	private ComponentMode	componentMode;
	private BeanSerializer	finalBeanSerializer;
	private ArraySerializer	componentArraySerializer;
	
	enum ComponentMode
	{
		ARRAY, //
		PRIMITIVE_INT, //
		PRIMITIVE_LONG, //
		PRIMITIVE_SHORT, //
		PRIMITIVE_FLOAT, //
		PRIMITIVE_DOUBLE, //
		PRIMITIVE_BYTE, //
		PRIMITIVE_BOOLEAN, //
		PRIMITIVE_CHAR, //
		FINAL, NUMBER, STRING, BOOLEAN, BEAN
	}
	
	@Override
	public void initialize(JsonProcessor jsonProcessor, Class<?> type)
	{
		this.jsonProcessor = jsonProcessor;
		Class<?> componentType = type.getComponentType();
		if (componentType.isArray())
		{
			componentMode = ComponentMode.ARRAY;
			try
			{
				componentArraySerializer = jsonProcessor.arraySerializerClass().newInstance();
				componentArraySerializer.initialize(jsonProcessor, componentType);
			}
			catch (Exception e)
			{
				throw new JustThrowException(e);
			}
		}
		else if (componentType.isPrimitive())
		{
			if (componentType == int.class)
			{
				componentMode = ComponentMode.PRIMITIVE_INT;
			}
			else if (componentType == short.class)
			{
				componentMode = ComponentMode.PRIMITIVE_SHORT;
			}
			else if (componentType == long.class)
			{
				componentMode = ComponentMode.PRIMITIVE_LONG;
			}
			else if (componentType == float.class)
			{
				componentMode = ComponentMode.PRIMITIVE_FLOAT;
			}
			else if (componentType == double.class)
			{
				componentMode = ComponentMode.PRIMITIVE_DOUBLE;
			}
			else if (componentType == byte.class)
			{
				componentMode = ComponentMode.PRIMITIVE_BYTE;
			}
			else if (componentType == boolean.class)
			{
				componentMode = ComponentMode.PRIMITIVE_BOOLEAN;
			}
			else if (componentType == char.class)
			{
				componentMode = ComponentMode.PRIMITIVE_CHAR;
			}
		}
		else if (Number.class.isAssignableFrom(componentType))
		{
			componentMode = ComponentMode.NUMBER;
		}
		else if (componentType == String.class)
		{
			componentMode = ComponentMode.STRING;
		}
		else if (componentType == Boolean.class)
		{
			componentMode = ComponentMode.BOOLEAN;
		}
		else if (Modifier.isFinal(componentType.getModifiers()))
		{
			componentMode = ComponentMode.FINAL;
			try
			{
				finalBeanSerializer = jsonProcessor.beanSerializerClass().newInstance();
				finalBeanSerializer.initialize(jsonProcessor, componentType);
			}
			catch (Exception e)
			{
				throw new JustThrowException(e);
			}
		}
		else
		{
			componentMode = ComponentMode.BEAN;
		}
	}
	
	@Override
	public boolean serialize(Object entity, StringOutput output)
	{
		if (entity == null)
		{
			return false;
		}
		output.append('[');
		boolean serialized = false;
		switch (componentMode)
		{
			case ARRAY:
			{
				Object[] array = (Object[]) entity;
				for (Object each : array)
				{
					if (componentArraySerializer.serialize(each, output))
					{
						serialized = true;
						output.append(',');
					}
				}
				break;
			}
			case PRIMITIVE_INT:
			{
				int[] array = (int[]) entity;
				for (int i : array)
				{
					output.append(i);
					serialized = true;
				}
				break;
			}
			case PRIMITIVE_BOOLEAN:
			{
				boolean[] array = (boolean[]) entity;
				for (boolean b : array)
				{
					output.append(b).append(',');
					serialized = true;
				}
				break;
			}
			case PRIMITIVE_BYTE:
			{
				byte[] array = (byte[]) entity;
				for (byte b : array)
				{
					output.append(b).append(',');
					serialized = true;
				}
				break;
			}
			case PRIMITIVE_CHAR:
			{
				char[] array = (char[]) entity;
				for (char c : array)
				{
					output.appendDoubleQuotes().append(c).appendDoubleQuotes().append(',');
					serialized = true;
				}
				break;
			}
			case PRIMITIVE_DOUBLE:
			{
				double[] array = (double[]) entity;
				for (double d : array)
				{
					output.append(d).append(',');
					serialized = true;
				}
				break;
			}
			case PRIMITIVE_FLOAT:
			{
				float[] array = (float[]) entity;
				for (float f : array)
				{
					output.append(f).append(',');
					serialized = true;
				}
				break;
			}
			case PRIMITIVE_LONG:
			{
				long[] array = (long[]) entity;
				for (long l : array)
				{
					output.append(l).append(',');
					serialized = true;
				}
				break;
			}
			case PRIMITIVE_SHORT:
			{
				short[] array = (short[]) entity;
				for (short s : array)
				{
					output.append(s).append(',');
					serialized = true;
				}
				break;
			}
			case BOOLEAN:
			{
				Boolean[] array = (Boolean[]) entity;
				for (Boolean each : array)
				{
					if (each != null)
					{
						output.append(each).append(',');
						serialized = true;
					}
					
				}
				break;
			}
			case NUMBER:
			{
				Number[] array = (Number[]) entity;
				for (Number each : array)
				{
					if (each != null)
					{
						output.append(each).append(',');
						serialized = true;
					}
				}
				break;
			}
			case STRING:
			{
				String[] array = (String[]) entity;
				for (String each : array)
				{
					if (each != null)
					{
						output.appendDoubleQuotes().append(each).appendDoubleQuotes().append(',');
						serialized = true;
					}
				}
				break;
			}
			case FINAL:
			{
				Object[] array = (Object[]) entity;
				for (Object each : array)
				{
					if (each != null)
					{
						if (finalBeanSerializer.serialize(each, output))
						{
							output.append(',');
							serialized = true;
						}
					}
				}
				break;
			}
			case BEAN:
			{
				Object[] array = (Object[]) entity;
				for (Object each : array)
				{
					if (each != null)
					{
						int length = output.length();
						jsonProcessor.serialize(each, output);
						if (length != output.length())
						{
							serialized = true;
						}
					}
				}
				break;
			}
			default:
				throw new NullPointerException();
		}
		if (serialized)
		{
			output.deleteLast();
		}
		output.append(']');
		return true;
	}
}
