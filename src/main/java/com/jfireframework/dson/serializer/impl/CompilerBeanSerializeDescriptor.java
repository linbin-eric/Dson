package com.jfireframework.dson.serializer.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.smc.SmcHelper;
import com.jfireframework.baseutil.smc.compiler.JavaStringCompiler;
import com.jfireframework.baseutil.smc.model.CompilerModel;
import com.jfireframework.baseutil.smc.model.FieldModel;
import com.jfireframework.baseutil.smc.model.MethodModel;
import com.jfireframework.dson.serializer.SerializeDescriptor;
import com.jfireframework.dson.serializer.Serializer;
import com.jfireframework.dson.strategy.SerializeDefinition;
import com.jfireframework.dson.util.StringOutput;

public class CompilerBeanSerializeDescriptor implements SerializeDescriptor
{
	private CompilerSerializer compilerSerializer;
	
	@Override
	public void serializeWithoutDoubleQuotes(Object entity, StringOutput output)
	{
		serialize(entity, output);
	}
	
	@Override
	public void serialize(Object entity, StringOutput output)
	{
		compilerSerializer.serialize(entity, output);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(Serializer serializer, Type type, Map<Type, SerializeDescriptor> map)
	{
		Class<?> beanType;
		if (type instanceof ParameterizedType)
		{
			beanType = (Class<?>) ((ParameterizedType) type).getRawType();
		}
		else if (type instanceof Class<?>)
		{
			beanType = (Class<?>) type;
		}
		else
		{
			throw new IllegalArgumentException();
		}
		CompilerModel compilerModel = SmcHelper.createClientClass(CompilerSerializer.class);
		try
		{
			Method method = CompilerSerializer.class.getMethod("serialize", Object.class, StringOutput.class);
			String constructorBody = "";
			MethodModel methodModel = new MethodModel(method);
			String body = SmcHelper.getTypeName(beanType) + " target = (" + SmcHelper.getTypeName(beanType) + ")$0;\r\n";
			body += "$1.append('{');\r\n";
			body += "int length = $1.length();\r\n";
			int count = 0;
			ClassLoader classLoader = beanType.getClassLoader();
			while (beanType != Object.class)
			{
				for (Method each : beanType.getDeclaredMethods())
				{
					String propertyName = getPropertyName(each);
					if (propertyName == null)
					{
						continue;
					}
					Class<?> returnType = each.getReturnType();
					try
					{
						Field field = beanType.getDeclaredField(propertyName);
						if (field.isAnnotationPresent(SerializeDefinition.class))
						{
							SerializeDescriptor serializeDescriptor = field.getAnnotation(SerializeDefinition.class).value().newInstance();
							serializeDescriptor.initialize(serializer, beanType, map);
							int index = serializer.registerSerializeDescriptor(serializeDescriptor);
							String fieldName = "field_" + (count++);
							FieldModel fieldModel = new FieldModel(fieldName, SerializeDescriptor.class);
							compilerModel.addField(fieldModel);
							constructorBody += fieldName + " = $0.get(" + index + ");\r\n";
							String name = "tmp_" + (count++);
							body += SmcHelper.getTypeName(returnType) + " " + name + " = target." + each.getName() + "();\r\n";
							body += "if(" + name + "!=null){\r\n";
							body += "\t$1.append(\"\\\"" + propertyName + "\\\":\");\r\n";
							body += fieldName + ".serialize(" + name + ",$1);\r\n";
							body += "$1.append(',');\r\n";
							body += "}\r\n";
							continue;
						}
					}
					catch (Exception e)
					{
						// 如果无法找到对应的field，则放弃本次的自定义序列化
						;
					}
					if (returnType == char.class)
					{
						body += "$1.append(\"\\\"" + propertyName + "\\\":\\\"\")"//
						        + ".append(target." + each.getName() + "()).append(\"\\\",\");\r\n";
					}
					else if (returnType.isPrimitive())
					{
						body += "$1.append(\"\\\"" + propertyName + "\\\":\")"//
						        + ".append(target." + each.getName() + "()).append(',');\r\n";
					}
					else if (returnType == String.class || returnType == Character.class)
					{
						String name = "tmp_" + (count++);
						body += SmcHelper.getTypeName(returnType) + " " + name + " = target." + each.getName() + "();\r\n";
						body += "if(" + name + "!=null){\r\n";
						// body += "\t" + name + "=" + name +
						// ".replace(\"\\\"\", \"\\\\\\\"\");\r\n";
						// body += "\t$1.append(\"\\\"" + propertyName +
						// "\\\":\\\"\").append(" + name +
						// ").append(\"\\\",\");\r\n";
						body += "\t$1.append(\"\\\"" + propertyName + "\\\":\\\"\");\r\n";
						body += "com.jfireframework.dson.util.WriterUtil.writeString($1," + name + ");\r\n";
						body += "\t$1.append(\"\\\",\");\r\n";
						body += "}\r\n";
					}
					else if (Number.class.isAssignableFrom(returnType) || returnType == Boolean.class)
					{
						String name = "tmp_" + (count++);
						body += SmcHelper.getTypeName(returnType) + " " + name + " = target." + each.getName() + "();\r\n";
						body += "if(" + name + "!=null){\r\n";
						body += "\t$1.append(\"\\\"" + propertyName + "\\\":\").append(" + name + ").append(',');\r\n";
						body += "}\r\n";
					}
					else if (Modifier.isFinal(returnType.getModifiers()))
					{
						String fieldName = "field_" + (count++);
						FieldModel fieldModel = new FieldModel(fieldName, SerializeDescriptor.class);
						compilerModel.addField(fieldModel);
						constructorBody += fieldName + " = $0.describe(" + SmcHelper.getTypeName(returnType) + ".class);\r\n";
						String name = "tmp_" + (count++);
						body += SmcHelper.getTypeName(returnType) + " " + name + " = target." + each.getName() + "();\r\n";
						body += "if(" + name + "!=null){\r\n";
						body += "\t$1.append(\"\\\"" + propertyName + "\\\":\");\r\n";
						body += fieldName + ".serialize(" + name + ",$1);\r\n";
						body += "$1.append(',');\r\n";
						body += "}\r\n";
					}
					else
					{
						String name = "tmp_" + (count++);
						body += SmcHelper.getTypeName(returnType) + " " + name + " = target." + each.getName() + "();\r\n";
						body += "if(" + name + "!=null){\r\n";
						body += "\t$1.append(\"\\\"" + propertyName + "\\\":\");\r\n";
						body += "serializer.serialize(" + name + ",$1);\r\n";
						body += "$1.append(',');\r\n";
						body += "}\r\n";
					}
				}
				beanType = beanType.getSuperclass();
			}
			body += "if(length!=$1.length()){$1.deleteLast();}\r\n";
			body += "$1.append('}');\r\n";
			compilerModel.addConstructor("super($0);\r\n" + constructorBody, Serializer.class);
			methodModel.setBody(body);
			compilerModel.putMethod(methodModel);
			JavaStringCompiler compiler = new JavaStringCompiler();
			Class<? extends CompilerSerializer> compile = (Class<? extends CompilerSerializer>) compiler.compile(compilerModel, classLoader);
			compilerSerializer = compile.getConstructor(Serializer.class).newInstance(serializer);
		}
		catch (Exception e)
		{
			throw new JustThrowException(e);
		}
	}
	
	private String getPropertyName(Method method)
	{
		if (method.getParameterTypes().length != 0)
		{
			return null;
		}
		if (method.getName().startsWith("get"))
		{
			String property = method.getName().substring(3);
			return property.substring(0, 1).toLowerCase() + property.substring(1);
		}
		else if (method.getName().startsWith("is"))
		{
			String property = method.getName().substring(2);
			return property.substring(0, 1).toLowerCase() + property.substring(1);
		}
		else
		{
			return null;
		}
	}
	
	public static abstract class CompilerSerializer
	{
		protected Serializer serializer;
		
		public CompilerSerializer(Serializer serializer)
		{
			this.serializer = serializer;
		}
		
		public abstract void serialize(Object entity, StringOutput output);
		
	}
	
}
