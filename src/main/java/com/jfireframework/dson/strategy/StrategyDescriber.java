package com.jfireframework.dson.strategy;

import com.jfireframework.dson.StringOutput;

public interface StrategyDescriber
{
    void serialize(Object value, StringOutput output);
}
