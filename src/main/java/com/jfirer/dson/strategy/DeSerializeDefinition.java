package com.jfirer.dson.strategy;

import com.jfirer.dson.reader.TypeReader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DeSerializeDefinition
{
    Class<? extends TypeReader> value();
}
