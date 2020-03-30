package com.jfirer.dson.writer;

public interface Writer
{
    /**
     * 将对象json输出到output中
     *
     * @param entity
     * @param output
     * @return
     */
    void toJson(Object entity, StringBuilder output);
}
