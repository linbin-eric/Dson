package com.jfirer.dson.reader;

import java.util.HashMap;
import java.util.Map;

public enum Symbol
{
    LEFT_BRACE('{'), //
    RIGHT_BRACE('}'), //
    LEFT_BRACKET('['), //
    RIGHT_BRACKET(']'), //
    COMMA(','), //
    COLON(':'), //
    NEWLINE('\n'), //
    RETURN('\r'), //
    BLANK(' '), //
    TAB('\t'), //
    DOUBLE_QUOTATION_MASK('"');//
    private static Map<Character, Symbol> symbols = new HashMap<Character, Symbol>(128);

    static
    {
        for (Symbol each : Symbol.values())
        {
            symbols.put(each.literals(), each);
        }
    }

    private final char literals;

    Symbol(char literals)
    {
        this.literals = literals;
    }

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
