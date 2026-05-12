package cc.jfire.dson.reader;

import cc.jfire.dson.reader.support.Node;
import cc.jfire.dson.reader.support.FieldIndexNode;
import cc.jfire.dson.reader.support.entry.ReadEntry;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Stream
{
    private       int                 offset = 0;
    private final int                 length;
    private final char[]              value;
    static        ThreadLocal<char[]> buf    = ThreadLocal.withInitial(() -> new char[128]);

    public Stream(String str)
    {
        length = str.length();
        char[] value = buf.get();
        if (str.length() <= value.length)
        {
            str.getChars(0, str.length(), value, 0);
        }
        else
        {
            value = new char[str.length()];
            buf.set(value);
            str.getChars(0, str.length(), value, 0);
        }
        this.value = value;
    }

    public char ignoreSymbol()
    {
        char[] value  = this.value;
        int    offset = this.offset;
        char   c      = value[offset];
        if (c > ' ')
        {
            return c;
        }
        int length = this.length;
        while (c == Symbol.BLANK.literals() //
               || c == Symbol.RETURN.literals()//
               || c == Symbol.NEWLINE.literals()//
               || c == Symbol.TAB.literals())
        {
            offset += 1;
            if (offset >= length)
            {
                this.offset = offset;
                return c;
            }
            c = value[offset];
            if (c > ' ')
            {
                this.offset = offset;
                return c;
            }
        }
        this.offset = offset;
        return c;
    }

    /**
     * offset 当前所在位置的内容是'"',处理完成后，offset的位置为结束的'"'位置+1
     *
     * @return
     */
    private String getString()
    {
        int    offset = this.offset;
        int    length = this.length;
        char[] value  = this.value;
        offset += 1;
        int  start = offset;
        char c;
        do
        {
            c = value[offset];
            if (c == Symbol.DOUBLE_QUOTATION_MASK.literals())
            {
                if (isRealDoubleQuotationMask(offset - 1) == false)
                {
                    offset += 1;
                }
                else
                {
                    break;
                }
            }
            else
            {
                offset += 1;
            }
        } while (offset < length);
        String result = new String(value, start, offset - start);
        offset += 1;
        this.offset = offset;
        return result;
    }

    private boolean isRealDoubleQuotationMask(int offsetOfLastSlash)
    {
        char[] value  = this.value;
        int    count  = 0;
        int    offset = offsetOfLastSlash;
        while (offset >= 0 && value[offset] == '\\')
        {
            count++;
            offset--;
        }
        return (count & 1) == 0;
    }

    /**
     * 当前位置为数字的起始，结束后位置为最后一位数字的位置+1
     *
     * @return
     */
    private Number getNumber()
    {
        int offset = this.offset;
        int length = this.length;
        int begin  = offset;
        offset += 1;
        char[]  value    = this.value;
        boolean hasDot   = false;
        boolean hasE     = false;
        boolean hasMinus = false;
        boolean hasplus  = false;
        do
        {
            char c = value[offset];
            if (c >= '0' && c <= '9')
            {
                offset += 1;
            }
            else if (c == '.')
            {
                offset += 1;
                hasDot = true;
            }
            else if ((c == 'e' || c == 'E') && hasE == false)
            {
                offset += 1;
                hasE = true;
            }
            else if (c == '-' && hasMinus == false && hasE == true)
            {
                hasMinus = true;
                offset += 1;
            }
            else if (c == '+' && hasplus == false && hasE == true)
            {
                hasplus = true;
                offset += 1;
            }
            else
            {
                break;
            }
        } while (offset < length);
        if (offset == length)
        {
            throw new IllegalArgumentException();
        }
        String numString = new String(value, begin, offset - begin);
        Number result    = null;
        if (hasDot || hasE || hasMinus || hasplus)
        {
            result = Double.valueOf(numString);
        }
        else
        {
            result = Long.valueOf(numString);
        }
        this.offset = offset;
        return result;
    }

    private void skipNumber()
    {
        int    offset = this.offset;
        int    length = this.length;
        char[] value  = this.value;
        offset += 1;
        boolean hasDot   = false;
        boolean hasE     = false;
        boolean hasMinus = false;
        boolean hasPlus  = false;
        do
        {
            char c = value[offset];
            if (c >= '0' && c <= '9')
            {
                offset += 1;
            }
            else if (c == '.' && hasDot == false)
            {
                offset += 1;
                hasDot = true;
            }
            else if ((c == 'e' || c == 'E') && hasE == false)
            {
                offset += 1;
                hasE = true;
            }
            else if (c == '-' && hasMinus == false && hasE == true)
            {
                hasMinus = true;
                offset += 1;
            }
            else if (c == '+' && hasPlus == false && hasE == true)
            {
                hasPlus = true;
                offset += 1;
            }
            else
            {
                break;
            }
        } while (offset < length);
        if (offset == length)
        {
            throw new IllegalArgumentException();
        }
        this.offset = offset;
    }

    public void startParseObject()
    {
        char c = ignoreSymbol();
        if (c != Symbol.LEFT_BRACE.literals())
        {
            throwExecption();
        }
        offset += 1;
    }

    public void startParseArray()
    {
        char c = ignoreSymbol();
        if (c != Symbol.LEFT_BRACKET.literals())
        {
            throwExecption();
        }
        offset += 1;
    }

    public boolean parseObjectEnd()
    {
        char c = ignoreSymbol();
        if (c == Symbol.RIGHT_BRACE.literals())
        {
            offset += 1;
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean parseArrayEnd()
    {
        char c = ignoreSymbol();
        if (c == Symbol.RIGHT_BRACKET.literals())
        {
            offset += 1;
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean isNextNullAndSkip()
    {
        char c = value[offset];
        if (offset + 3 < length)
        {
            char c1 = value[offset + 1];
            char c2 = value[offset + 2];
            char c3 = value[offset + 3];
            if ((c == 'N' || c == 'n') && (c1 == 'U' || c1 == 'u') && (c2 == 'L' || c2 == 'l') && (c3 == 'L' || c3 == 'l'))
            {
                offset += 4;
                return true;
            }
        }
        return false;
    }

    public ReadEntry getName(Node node)
    {
        char c = value[offset];
        if (c != Symbol.DOUBLE_QUOTATION_MASK.literals())
        {
            throwExecption();
        }
        offset += 1;
        do
        {
            c = value[offset];
            if (c == Symbol.DOUBLE_QUOTATION_MASK.literals())
            {
                if (isRealDoubleQuotationMask(offset - 1) == false)
                {
                    node = node.getNext(c);
                    offset += 1;
                }
                else
                {
                    offset += 1;
                    return node.getEntry();
                }
            }
            else
            {
                node = node.getNext(c);
                offset += 1;
            }
        } while (offset < length && node != null);
        if (offset == length)
        {
            return null;
        }
        do
        {
            c = value[offset];
            if (c == Symbol.DOUBLE_QUOTATION_MASK.literals())
            {
                if (isRealDoubleQuotationMask(offset - 1) == false)
                {
                    offset += 1;
                }
                else
                {
                    offset += 1;
                    return null;
                }
            }
            else
            {
                offset += 1;
            }
        } while (offset < length);
        return null;
    }

    public int getNameIndex(FieldIndexNode node)
    {
        char c = value[offset];
        if (c != Symbol.DOUBLE_QUOTATION_MASK.literals())
        {
            throwExecption();
        }
        offset += 1;
        do
        {
            c = value[offset];
            if (c == Symbol.DOUBLE_QUOTATION_MASK.literals())
            {
                if (isRealDoubleQuotationMask(offset - 1) == false)
                {
                    node = node == null ? null : node.getNext(c);
                    offset += 1;
                }
                else
                {
                    offset += 1;
                    return node == null ? -1 : node.getIndex();
                }
            }
            else
            {
                node = node == null ? null : node.getNext(c);
                offset += 1;
            }
        } while (offset < length && node != null);
        if (offset == length)
        {
            return -1;
        }
        do
        {
            c = value[offset];
            if (c == Symbol.DOUBLE_QUOTATION_MASK.literals())
            {
                if (isRealDoubleQuotationMask(offset - 1) == false)
                {
                    offset += 1;
                }
                else
                {
                    offset += 1;
                    return -1;
                }
            }
            else
            {
                offset += 1;
            }
        } while (offset < length);
        return -1;
    }

    public String getName()
    {
        char c = value[offset];
        if (c != Symbol.DOUBLE_QUOTATION_MASK.literals())
        {
            throwExecption();
        }
        return getString();
    }

    public void skipColon()
    {
        if ((ignoreSymbol()) != Symbol.COLON.literals())
        {
            throwExecption();
        }
        offset += 1;
    }

    public char getChar()
    {
        if (ignoreSymbol() != Symbol.DOUBLE_QUOTATION_MASK.literals())
        {
            throwExecption();
        }
        int  offset = this.offset;
        char c      = value[offset + 1];
        if (value[offset + 2] != Symbol.DOUBLE_QUOTATION_MASK.literals())
        {
            throwExecption();
        }
        this.offset = offset + 3;
        return c;
    }

    public long getLong()
    {
        return getIntegralLong();
    }

    public int getInt()
    {
        long value = getIntegralLong();
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE)
        {
            throw new NumberFormatException();
        }
        return (int) value;
    }

    private long getIntegralLong()
    {
        char   c      = ignoreSymbol();
        char[] value  = this.value;
        int    length = this.length;
        int    offset = this.offset;
        boolean negative = c == '-';
        if (negative)
        {
            offset += 1;
            if (offset >= length)
            {
                throwExecption();
            }
        }
        long limit   = negative ? Long.MIN_VALUE : -Long.MAX_VALUE;
        long multmin = limit / 10;
        long result  = 0;
        boolean hasDigit = false;
        while (offset < length)
        {
            c = value[offset];
            if (c < '0' || c > '9')
            {
                break;
            }
            int digit = c - '0';
            if (result < multmin)
            {
                throw new NumberFormatException();
            }
            result *= 10;
            if (result < limit + digit)
            {
                throw new NumberFormatException();
            }
            result -= digit;
            offset += 1;
            hasDigit = true;
        }
        if (hasDigit == false)
        {
            throwExecption();
        }
        if (offset < length)
        {
            c = value[offset];
            if (c == '.' || c == 'e' || c == 'E' || c == '+')
            {
                throw new NumberFormatException();
            }
        }
        this.offset = offset;
        return negative ? result : -result;
    }

    private String getNumberString()
    {
        ignoreSymbol();
        char[] value  = this.value;
        int    length = this.length;
        int    offset = this.offset;
        int    begin  = offset;
        offset += 1;
        boolean hasDot   = false;
        boolean hasE     = false;
        boolean hasMinus = false;
        boolean hasplus  = false;
        do
        {
            char c = value[offset];
            if (c >= '0' && c <= '9')
            {
                offset += 1;
            }
            else if (c == '.')
            {
                offset += 1;
                hasDot = true;
            }
            else if ((c == 'e' || c == 'E') && hasE == false)
            {
                offset += 1;
                hasE = true;
            }
            else if (c == '-' && hasMinus == false && hasE == true)
            {
                hasMinus = true;
                offset += 1;
            }
            else if (c == '+' && hasplus == false && hasE == true)
            {
                hasplus = true;
                offset += 1;
            }
            else
            {
                break;
            }
        } while (offset < length);
        if (offset == length)
        {
            throwExecption();
        }
        this.offset = offset;
        return new String(value, begin, offset - begin);
    }

    public boolean skipComma()
    {
        char c = ignoreSymbol();
        if (c == Symbol.COMMA.literals())
        {
            offset += 1;
            ignoreSymbol();
            return true;
        }
        return false;
    }

    public Object readUnKnowType()
    {
        if (isNextNullAndSkip())
        {
            return null;
        }
        char c = ignoreSymbol();
        if (c == Symbol.LEFT_BRACE.literals())
        {
            offset += 1;
            Map map = new HashMap();
            while (parseObjectEnd() == false)
            {
                String name = getName();
                skipColon();
                map.put(name, readUnKnowType());
                skipComma();
            }
            return map;
        }
        else if (c == Symbol.LEFT_BRACKET.literals())
        {
            offset += 1;
            List list = new LinkedList();
            while (parseArrayEnd() == false)
            {
                list.add(readUnKnowType());
                skipComma();
            }
            return list;
        }
        else if (c == Symbol.DOUBLE_QUOTATION_MASK.literals() && isRealDoubleQuotationMask(offset - 1))
        {
            String value = getString();
            return value;
        }
        else if (c == 'T' || c == 't')
        {
            String value = new String(this.value, offset, 4);
            if ("TRUE".equals(value) || "true".equals(value))
            {
                offset += 4;
                return true;
            }
            else
            {
                throw new IllegalArgumentException();
            }
        }
        else if (c == 'F' || c == 'f')
        {
            String value = new String(this.value, offset, 5);
            if ("false".equals(value) || "FALSE".equals(value))
            {
                offset += 5;
                return false;
            }
            else
            {
                throw new IllegalArgumentException();
            }
        }
        else if (c == 'n' || c == 'N')
        {
            String value = new String(this.value, offset, 4);
            if ("null".equals(value) || "NULL".equals(value))
            {
                offset += 4;
                return null;
            }
            else
            {
                throw new IllegalArgumentException();
            }
        }
        else if ((c >= '0' && c <= '9') || c == '-')
        {
            return getNumber();
        }
        else
        {
            throw new IllegalArgumentException("非法字符:" + c + ",当前解析进度:" + new String(this.value, offset, length - offset));
        }
    }

    public String getWholeValueAsString()
    {
        ignoreSymbol();
        int start = this.offset;
        skipWholeValue();
        int end = this.offset;
        return String.valueOf(this.value, start, end - start);
    }

    public void skipWholeValue()
    {
        char   c     = ignoreSymbol();
        char[] value = this.value;
        if (c == Symbol.LEFT_BRACE.literals())
        {
            int numOfLeftBrace = 1;
            offset += 1;
            boolean inStringState = false;
            while (((c = value[offset]) == Symbol.RIGHT_BRACE.literals() && numOfLeftBrace == 1 && inStringState == false) == false)
            {
                if (inStringState)
                {
                    if (c == Symbol.DOUBLE_QUOTATION_MASK.literals())
                    {
                        if (isRealDoubleQuotationMask(offset - 1) == false)
                        {
                            ;
                        }
                        else
                        {
                            inStringState = false;
                        }
                    }
                }
                else
                {
                    if (c == Symbol.DOUBLE_QUOTATION_MASK.literals())
                    {
                        if (isRealDoubleQuotationMask(offset - 1) == false)
                        {
                            ;
                        }
                        else
                        {
                            inStringState = true;
                        }
                    }
                    else if (c == Symbol.LEFT_BRACE.literals())
                    {
                        numOfLeftBrace++;
                    }
                    else if (c == Symbol.RIGHT_BRACE.literals())
                    {
                        numOfLeftBrace -= 1;
                    }
                }
                offset += 1;
            }
            offset += 1;
        }
        else if (c == Symbol.LEFT_BRACKET.literals())
        {
            int numOfLeftBracket = 1;
            offset += 1;
            boolean inStringState = false;
            while (((c = value[offset]) == Symbol.RIGHT_BRACKET.literals() && inStringState == false && numOfLeftBracket == 1) == false)
            {
                if (inStringState)
                {
                    if (c == Symbol.DOUBLE_QUOTATION_MASK.literals() && isRealDoubleQuotationMask(offset - 1))
                    {
                        inStringState = false;
                    }
                }
                else
                {
                    if (c == Symbol.DOUBLE_QUOTATION_MASK.literals() && isRealDoubleQuotationMask(offset - 1))
                    {
                        inStringState = true;
                    }
                    else if (c == Symbol.LEFT_BRACKET.literals())
                    {
                        numOfLeftBracket++;
                    }
                    else if (c == Symbol.RIGHT_BRACKET.literals())
                    {
                        numOfLeftBracket -= 1;
                    }
                }
                offset += 1;
            }
            offset += 1;
        }
        else if (c == Symbol.DOUBLE_QUOTATION_MASK.literals())
        {
            offset += 1;
            while ((value[offset] == Symbol.DOUBLE_QUOTATION_MASK.literals() && isRealDoubleQuotationMask(offset - 1)) == false)
            {
                offset += 1;
            }
            offset += 1;
        }
        else if (c == 'T' || c == 't')
        {
            offset += 4;
        }
        else if (c == 'F' || c == 'f')
        {
            offset += 5;
        }
        else if (c == 'n' || c == 'N')
        {
            offset += 4;
        }
        else if ((c >= '0' && c <= '9') || c == '-')
        {
            skipNumber();
        }
    }

    private void throwExecption()
    {
        if (offset > 20)
        {
            throw new IllegalStateException(offset + " : " + new String(value, offset - 20, 20));
        }
        else
        {
            throw new IllegalStateException(offset + " : " + new String(value, 0, offset));
        }
    }

    public String getStringValue()
    {
        if (ignoreSymbol() != Symbol.DOUBLE_QUOTATION_MASK.literals())
        {
            throw new IllegalStateException("在" + offset + "位置，类型应该为字符串，当前不是，需要检查 : " + new String(value, 0, offset));
        }
        return getString();
    }

    public short getShort()
    {
        long value = getIntegralLong();
        if (value < Short.MIN_VALUE || value > Short.MAX_VALUE)
        {
            throw new NumberFormatException();
        }
        return (short) value;
    }

    public byte getByte()
    {
        long value = getIntegralLong();
        if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE)
        {
            throw new NumberFormatException();
        }
        return (byte) value;
    }

    public boolean getBoolean()
    {
        char c = ignoreSymbol();
        if (c == 'T' || c == 't')
        {
            String value = new String(this.value, offset, 4);
            if ("TRUE".equals(value) || "true".equals(value))
            {
                offset += 4;
                return true;
            }
            else
            {
                throwExecption();
            }
        }
        else if (c == 'F' || c == 'f')
        {
            String value = new String(this.value, offset, 5);
            if ("false".equals(value) || "FALSE".equals(value))
            {
                offset += 5;
                return false;
            }
            else
            {
                throwExecption();
            }
        }
        throwExecption();
        return false;
    }

    public float getFloat()
    {
        return Float.parseFloat(getNumberString());
    }

    public double getDouble()
    {
        return Double.parseDouble(getNumberString());
    }

    public Integer getWInt()
    {
        return Integer.valueOf(getInt());
    }

    public Byte getWByte()
    {
        return Byte.valueOf(getByte());
    }

    public Long getWLong()
    {
        return Long.valueOf(getLong());
    }

    public Float getWFloat()
    {
        return Float.valueOf(getNumberString());
    }

    public Short getWShort()
    {
        return Short.valueOf(getShort());
    }

    public Double getWDouble()
    {
        return Double.valueOf(getNumberString());
    }

    public String errorPosition()
    {
        return offset + 20 > length ? new String(value, offset, length - offset) : new String(value, offset, 20);
    }
}
