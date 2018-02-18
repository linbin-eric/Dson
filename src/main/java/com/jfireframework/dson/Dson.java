package com.jfireframework.dson;

public class Dson
{
    private static JsonProcessor defaultProcessor;
    static
    {
        defaultProcessor = new JsonProcessorImpl();
        defaultProcessor.initialize(null);
    }
    
    public static final JsonProcessor defaultProcessor()
    {
        return defaultProcessor;
    }
}
