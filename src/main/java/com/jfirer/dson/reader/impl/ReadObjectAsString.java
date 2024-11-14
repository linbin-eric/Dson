package com.jfirer.dson.reader.impl;

import com.jfirer.dson.reader.Stream;
import com.jfirer.dson.reader.TypeReader;

public class ReadObjectAsString implements TypeReader
{
    @Override
    public Object fromString(Stream stream)
    {
        return stream.getWholeValueAsString();
    }
}
