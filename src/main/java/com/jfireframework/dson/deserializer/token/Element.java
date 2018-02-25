package com.jfireframework.dson.deserializer.token;

class Element
{
    Object        value;
    JsonValueType valueType;
    
    public Element(Object value, JsonValueType valueType)
    {
        this.value = value;
        this.valueType = valueType;
    }
    
}