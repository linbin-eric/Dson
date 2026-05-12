package cc.jfire.dson.dynamic;

public interface DynamicModifyValue
{
    /**
     * 对该对象的原始输出进行修改
     * @param entity
     * @param origin
     * @return
     */
    default String modify(Object entity, String origin)
    {
        return origin;
    }
}
