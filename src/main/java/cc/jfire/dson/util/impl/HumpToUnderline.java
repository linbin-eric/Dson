package cc.jfire.dson.util.impl;

import cc.jfire.dson.util.JsonRenameStrategy;

import java.lang.reflect.Field;

/**
 * 驼峰转下划线
 */
public class HumpToUnderline implements JsonRenameStrategy
{
    /**
     * 将驼峰名称转化为下划线并且返回
     *
     * @param field
     * @return
     */
    @Override
    public String name(Field field)
    {
        String lowerCase = field.getName().replaceAll("([A-Z])", "_$1").toLowerCase();
        return lowerCase.charAt(0) == '_' ? lowerCase.substring(1) : lowerCase;
    }
}
