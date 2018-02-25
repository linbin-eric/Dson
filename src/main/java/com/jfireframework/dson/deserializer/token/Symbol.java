package com.jfireframework.dson.deserializer.token;

import java.util.HashMap;
import java.util.Map;

public enum Symbol implements TokenType
{
    LEFT_BRACE('{'), //
    RIGHT_BRACE('}'), //
    LEFT_BRACKET('['), //
    RIGHT_BRACKET(']'), //
    COMMA(','), //
    COLON(':'), //
    DOUBLE_QUOTATION_MASK('"');//
    private static Map<Character, Symbol> symbols = new HashMap<Character, Symbol>(128);
    
    static
    {
        for (Symbol each : Symbol.values())
        {
            symbols.put(each.literals(), each);
        }
    }
    
    private Symbol(char literals)
    {
        this.literals = literals;
    }
    
    private final char literals;
    
    /**
     * 通过字面量查找词法符号.
     * 
     * @param literals 字面量
     * @return 词法符号
     */
    public static Symbol literalsOf(final char literals)
    {
        return symbols.get(literals);
    }
    
    public char literals()
    {
        return literals;
    }
}
