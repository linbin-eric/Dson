package com.jfireframework.dson.strategy;

import com.jfireframework.dson.util.StringOutput;

public interface StrategyDescriber
{
    void serialize(Object value, StringOutput output);
}
