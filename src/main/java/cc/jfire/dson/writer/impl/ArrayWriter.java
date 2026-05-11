package cc.jfire.dson.writer.impl;

import cc.jfire.dson.DsonContext;
import cc.jfire.dson.writer.TypeWriter;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ArrayWriter
{
    public static TypeWriter findSuitableArrayWriter(Type type)
    {
        if (type instanceof GenericArrayType)
        {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            if (componentType instanceof GenericArrayType)
            {
                return new ComponentFinalTypeArrayWriter();
            }
            else if (componentType instanceof ParameterizedType)
            {
                if (Modifier.isFinal(((Class) ((ParameterizedType) componentType).getRawType()).getModifiers()))
                {
                    return new ComponentFinalTypeArrayWriter();
                }
                else
                {
                    return new ComponentUnFinalTypeArrayWriter();
                }
            }
            else
            {
                throw new IllegalArgumentException();
            }
        }
        else if (type instanceof Class<?>)
        {
            Class<?> componentType = ((Class<?>) type).getComponentType();
            if (Modifier.isFinal(componentType.getModifiers()))
            {
                return new ComponentFinalTypeArrayWriter();
            }
            else
            {
                return new ComponentUnFinalTypeArrayWriter();
            }
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }

    static class ComponentFinalTypeArrayWriter implements TypeWriter
    {
        private TypeWriter componentWriter;

        @Override
        public void initialize(Type type, DsonContext dsonContext)
        {
            if (type instanceof GenericArrayType)
            {
                Type componentType = ((GenericArrayType) type).getGenericComponentType();
                if (componentType instanceof ParameterizedType)
                {
                    Class ckazz = (Class) ((ParameterizedType) componentType).getRawType();
                    if (Modifier.isFinal(ckazz.getModifiers()))
                    {
                        componentWriter = dsonContext.parseWriter(componentType);
                    }
                    else
                    {
                        throw new IllegalStateException();
                    }
                }
                else if (componentType instanceof GenericArrayType)
                {
                    componentWriter = dsonContext.parseWriter(componentType);
                }
                else
                {
                    throw new IllegalStateException();
                }
            }
            else if (type instanceof Class<?> ckazz)
            {
                if (Modifier.isFinal(ckazz.getComponentType().getModifiers()))
                {
                    componentWriter = dsonContext.parseWriter(ckazz.getComponentType());
                }
                else
                {
                    throw new IllegalStateException();
                }
            }
        }

        @Override
        public void toJson(Object entity, StringBuilder output)
        {
            if (entity == null)
            {
                return;
            }
            output.append('[');
            Object[]   elements   = (Object[]) entity;
            int        length     = elements.length;
            TypeWriter typeWriter = this.componentWriter;
            boolean    hasComma   = false;
            for (int i = 0; i < length; i++)
            {
                Object element = elements[i];
                if (element != null)
                {
                    typeWriter.toJson(element, output);
                    output.append(',');
                    hasComma = true;
                }
            }
            if (hasComma)
            {
                output.setLength(output.length() - 1);
            }
            output.append(']');
        }

        @Override
        public Object toJsonObject(Object entity)
        {
            if (entity == null)
            {
                return null;
            }
            List<Object> list       = new ArrayList<>();
            Object[]     elements   = (Object[]) entity;
            TypeWriter   typeWriter = this.componentWriter;
            for (Object element : elements)
            {
                if (element != null)
                {
                    list.add(typeWriter.toJsonObject(element));
                }
            }
            return list;
        }
    }

    static class ComponentUnFinalTypeArrayWriter implements TypeWriter
    {
        private DsonContext dsonContext;

        @Override
        public void initialize(Type type, DsonContext dsonContext)
        {
            this.dsonContext = dsonContext;
        }

        @Override
        public void toJson(Object entity, StringBuilder output)
        {
            if (entity == null)
            {
                return;
            }
            output.append('[');
            Object[] elements = (Object[]) entity;
            int      length   = elements.length;
            boolean  hasComma = false;
            for (int i = 0; i < length; i++)
            {
                Object element = elements[i];
                if (element != null)
                {
                    dsonContext.parseWriter(element.getClass()).toJson(element, output);
                    output.append(',');
                    hasComma = true;
                }
            }
            if (hasComma)
            {
                output.setLength(output.length() - 1);
            }
            output.append(']');
        }

        @Override
        public Object toJsonObject(Object entity)
        {
            if (entity == null)
            {
                return null;
            }
            List<Object> list     = new ArrayList<>();
            Object[]     elements = (Object[]) entity;
            for (Object element : elements)
            {
                if (element != null)
                {
                    list.add(dsonContext.parseWriter(element.getClass()).toJsonObject(element));
                }
            }
            return list;
        }
    }
}
