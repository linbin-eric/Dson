package com.jfirer.dson.writer.impl;

import com.jfirer.dson.writer.JsonWriter;
import com.jfirer.dson.writer.TypeWriter;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ArrayWriter implements TypeWriter
{
    private JsonWriter jsonWriter;
    private ToJson     toJson;

    public ArrayWriter(JsonWriter serializer, Type type)
    {
        initialize(serializer, type);
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
            int length = output.length();
            for (int element : (int[]) array)
            {
                output.append(element).append(',');
            }
            if (length != output.length())
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
            int length = output.length();
            for (boolean element : (boolean[]) array)
            {
                output.append(element).append(',');
            }
            if (length != output.length())
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
            int length = output.length();
            for (byte element : (byte[]) array)
            {
                output.append(element).append(',');
            }
            if (length != output.length())
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
            int length = output.length();
            for (long element : (long[]) array)
            {
                output.append(element).append(',');
            }
            if (length != output.length())
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
            int length = output.length();
            for (short element : (short[]) array)
            {
                output.append(element).append(',');
            }
            if (length != output.length())
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
            int length = output.length();
            for (float element : (float[]) array)
            {
                output.append(element).append(',');
            }
            if (length != output.length())
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
            int length = output.length();
            for (double element : (double[]) array)
            {
                output.append(element).append(',');
            }
            if (length != output.length())
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
            int length = output.length();
            for (char element : (char[]) array)
            {
                output.append('"').append(element).append("\",");
            }
            if (length != output.length())
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
            int length = output.length();
            for (String element : (String[]) array)
            {
                if (element != null)
                {
                    output.append('"').append(element).append("\",");
                }
                else
                {
                    output.append("null,");
                }
            }
            if (length != output.length())
            {
                output.setLength(output.length() - 1);
            }
            output.append(']');
        }
    }

    class FinalElementTypeArrayToJson implements ToJson
    {
        private TypeWriter typeWriter;
        private Type       componentType;

        public FinalElementTypeArrayToJson(Type componentType)
        {
            this.componentType = componentType;
        }

        @Override
        public void output(StringBuilder output, Object array)
        {
            output.append('[');
            Object[]   elements   = (Object[]) array;
            int        length     = elements.length;
            TypeWriter typeWriter = this.typeWriter;
            if (typeWriter == null)
            {
                this.typeWriter = typeWriter = jsonWriter.get(componentType);
            }
            boolean hasComma = false;
            for (int i = 0; i < length; i++)
            {
                Object element = elements[i];
                if (element != null)
                {
                    typeWriter.toJson(element, output);
                    output.append(',');
                }
                else
                {
                    output.append("null,");
                }
                hasComma = true;
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
                    jsonWriter.toJson(element, output);
                    output.append(',');
                }
                else
                {
                    output.append("null,");
                }
                hasComma = true;
            }
            if (hasComma)
            {
                output.setLength(output.length() - 1);
            }
            output.append(']');
        }
    }

    @Override
    public void initialize(JsonWriter serializer, Type type)
    {
        this.jsonWriter = serializer;
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
