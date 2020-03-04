package com.jfirer.dson.strategy;

import com.jfirer.dson.serializer.TypeWriter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SerializeDefinition
{
    Class<? extends TypeWriter> value();
}
