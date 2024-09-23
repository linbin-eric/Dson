package com.jfirer.dson.writer.impl;

import com.jfirer.dson.DsonContext;
import com.jfirer.dson.writer.TypeWriter;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ArrayWriter implements TypeWriter
{
    private DsonContext dsonContext;
    private ToJson      toJson;

    public ArrayWriter(DsonContext dsonContext, Type type)
    {
        initialize(type, dsonContext);
    }

    public ArrayWriter()
    {
    }

    interface ToJson
    {
        void output(StringBuilder builder, Object array);
    }

    class intArrayToJson implements ToJson
    {
        @Override
        public void output(StringBuilder output, Object array)
        {
            output.append('[');
            int[] arr = (int[]) array;
            for (int element : (int[]) array)
            {
                output.append(element).append(',');
            }
            if (arr.length != 0)
            {
                output.setLength(output.length() - 1);
            }
            output.append(']');
        }
    }

    class booleanArrayToJson implements ToJson
    {
        @Override
        public void output(StringBuilder output, Object array)
        {
            output.append('[');
            boolean[] arr = (boolean[]) array;
            for (boolean element : (boolean[]) array)
            {
                output.append(element).append(',');
            }
            if (arr.length != 0)
            {
                output.setLength(output.length() - 1);
            }
            output.append(']');
        }
    }

    class byteArrayToJson implements ToJson
    {
        @Override
        public void output(StringBuilder output, Object array)
        {
            output.append('[');
            byte[] arr = (byte[]) array;
            for (byte element : (byte[]) array)
            {
                output.append(element).append(',');
            }
            if (arr.length != 0)
            {
                output.setLength(output.length() - 1);
            }
            output.append(']');
        }
    }

    class longArrayToJson implements ToJson
    {
        @Override
        public void output(StringBuilder output, Object array)
        {
            output.append('[');
            long[] arr = (long[]) array;
            for (long element : (long[]) array)
            {
                output.append(element).append(',');
            }
            if (arr.length != 0)
            {
                output.setLength(output.length() - 1);
            }
            output.append(']');
        }
    }

    class shortArrayToJson implements ToJson
    {
        @Override
        public void output(StringBuilder output, Object array)
        {
            output.append('[');
            short[] arr = (short[]) array;
            for (short element : (short[]) array)
            {
                output.append(element).append(',');
            }
            if (arr.length != 0)
            {
                output.setLength(output.length() - 1);
            }
            output.append(']');
        }
    }

    class floatArrayToJson implements ToJson
    {
        @Override
        public void output(StringBuilder output, Object array)
        {
            output.append('[');
            float[] arr = (float[]) array;
            for (float element : (float[]) array)
            {
                output.append(element).append(',');
            }
            if (arr.length != 0)
            {
                output.setLength(output.length() - 1);
            }
            output.append(']');
        }
    }

    class doubleArrayToJson implements ToJson
    {
        @Override
        public void output(StringBuilder output, Object array)
        {
            output.append('[');
            double[] arr = (double[]) array;
            for (double element : (double[]) array)
            {
                output.append(element).append(',');
            }
            if (arr.length != 0)
            {
                output.setLength(output.length() - 1);
            }
            output.append(']');
        }
    }

    class charArrayToJson implements ToJson
    {
        @Override
        public void output(StringBuilder output, Object array)
        {
            output.append('[');
            char[] arr = (char[]) array;
            for (char element : arr)
            {
                output.append('"').append(element).append("\",");
            }
            if (arr.length != 0)
            {
                output.setLength(output.length() - 1);
            }
            output.append(']');
        }
    }

    class StringArrayToJson implements ToJson
    {
        @Override
        public void output(StringBuilder output, Object array)
        {
            output.append('[');
            boolean hasComma = false;
            for (String element : (String[]) array)
            {
                if (element != null)
                {
                    output.append('"').append(element).append("\",");
                    hasComma = true;
                }
            }
            if (hasComma)
            {
                output.setLength(output.length() - 1);
            }
            output.append(']');
        }
    }

    class FinalElementTypeArrayToJson implements ToJson
    {
        private TypeWriter typeWriter;

        public FinalElementTypeArrayToJson(Type componentType)
        {
            typeWriter = dsonContext.parseWriter(componentType);
        }

        @Override
        public void output(StringBuilder output, Object array)
        {
            output.append('[');
            Object[]   elements   = (Object[]) array;
            int        length     = elements.length;
            TypeWriter typeWriter = this.typeWriter;
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
    }

    class UnFinalElementTypeArrayToJson implements ToJson
    {
        @Override
        public void output(StringBuilder output, Object array)
        {
            output.append('[');
            boolean hasComma = false;
            for (Object element : (Object[]) array)
            {
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
    }

    @Override
    public void initialize(Type type, DsonContext dsonContext)
    {
        this.dsonContext = dsonContext;
        if (type instanceof Class<?>)
        {
            Class<?> componentType = ((Class<?>) type).getComponentType();
            if (componentType == int.class)
            {
                toJson = new intArrayToJson();
            }
            else if (componentType == short.class)
            {
                toJson = new shortArrayToJson();
            }
            else if (componentType == long.class)
            {
                toJson = new longArrayToJson();
            }
            else if (componentType == float.class)
            {
                toJson = new floatArrayToJson();
            }
            else if (componentType == double.class)
            {
                toJson = new doubleArrayToJson();
            }
            else if (componentType == boolean.class)
            {
                toJson = new booleanArrayToJson();
            }
            else if (componentType == byte.class)
            {
                toJson = new byteArrayToJson();
            }
            else if (componentType == char.class)
            {
                toJson = new charArrayToJson();
            }
            else if (componentType == String.class)
            {
                toJson = new StringArrayToJson();
            }
            else if (Modifier.isFinal(componentType.getModifiers()))
            {
                toJson = new FinalElementTypeArrayToJson(componentType);
            }
            else
            {
                toJson = new UnFinalElementTypeArrayToJson();
            }
        }
        else if (type instanceof GenericArrayType)
        {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            if (componentType instanceof ParameterizedType)
            {
                Type rawType = ((ParameterizedType) componentType).getRawType();
                if (rawType instanceof Class && Modifier.isFinal(((Class) rawType).getModifiers()))
                {
                    toJson = new FinalElementTypeArrayToJson(componentType);
                    return;
                }
            }
            else if (componentType instanceof GenericArrayType)
            {
                toJson = new FinalElementTypeArrayToJson(componentType);
                return;
            }
            toJson = new UnFinalElementTypeArrayToJson();
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void toJson(Object entity, StringBuilder output)
    {
        if (entity == null)
        {
            return;
        }
        toJson.output(output, entity);
    }
}
