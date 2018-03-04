package com.jfireframework.dson.metadata.parse;

import com.jfireframework.dson.metadata.json.DsonObject;
import com.jfireframework.dson.metadata.json.JsonArray;
import com.jfireframework.dson.metadata.json.JsonCollection;
import com.jfireframework.dson.metadata.json.JsonValueType;

public class Lexer
{
	
	private int		offset	= 0;
	private String	str;
	
	public Lexer(String str)
	{
		this.str = str;
	}
	
	public DsonObject parse()
	{
		offset = 0;
		char c = str.charAt(offset);
		if (c == Symbol.LEFT_BRACE.literals())
		{
			return parseCollection();
		}
		else if (c == Symbol.LEFT_BRACKET.literals())
		{
			return parseArray();
		}
		else
		{
			throw new IllegalArgumentException();
		}
	}
	
	private JsonArray parseArray()
	{
		JsonArray jsonArray = new JsonArray();
		char c = str.charAt(offset);
		if (c != Symbol.LEFT_BRACKET.literals())
		{
			throw new IllegalArgumentException();
		}
		offset += 1;
		while (offset < str.length())
		{
			c = str.charAt(offset);
			if (c == Symbol.DOUBLE_QUOTATION_MASK.literals())
			{
				String value = getString(str);
				jsonArray.add(value, JsonValueType.STRING);
			}
			else if (c >= '0' && c <= '9')
			{
				Number number = getNumber(str);
				if (number instanceof Double)
				{
					jsonArray.add(number, JsonValueType.NUMBER_DOUBLE);
				}
				else
				{
					jsonArray.add(number, JsonValueType.NUMBER_LONG);
				}
			}
			else if (c == 'T' || c == 't')
			{
				String value = str.substring(offset, offset + 4);
				if ("TRUE".equals(value) || "true".equals(value))
				{
					jsonArray.add(true, JsonValueType.BOOLEAN);
					offset += 4;
				}
				else
				{
					throw new IllegalArgumentException();
				}
			}
			else if (c == 'F' || c == 'f')
			{
				String value = str.substring(offset, offset + 5);
				if ("false".equals(value) || "FALSE".equals(value))
				{
					jsonArray.add(false, JsonValueType.BOOLEAN);
					offset += 5;
				}
				else
				{
					throw new IllegalArgumentException();
				}
			}
			else if (c == Symbol.LEFT_BRACE.literals())
			{
				JsonCollection parseCollection = parseCollection();
				jsonArray.add(parseCollection, JsonValueType.COLLECTION);
				offset += 1;
			}
			else if (c == Symbol.LEFT_BRACKET.literals())
			{
				JsonArray array = parseArray();
				jsonArray.add(array, JsonValueType.ARRAY);
				offset += 1;
			}
			else if (c == Symbol.RIGHT_BRACKET.literals())
			{
				break;
			}
			else
			{
				throw new IllegalArgumentException("非法字符:" + c + "当前解析剩余内容:" + str.substring(offset));
			}
			c = str.charAt(offset);
			if (c == Symbol.RIGHT_BRACKET.literals())
			{
				break;
			}
			if (c != Symbol.COMMA.literals())
			{
				throw new IllegalArgumentException();
			}
			offset += 1;
		}
		return jsonArray;
	}
	
	/**
	 * 此时offset所在位置为{
	 * 
	 * @param str
	 * @param offset
	 * @return
	 */
	private JsonCollection parseCollection()
	{
		JsonCollection jsonCollection = new JsonCollection();
		char c = str.charAt(offset);
		if (c != Symbol.LEFT_BRACE.literals())
		{
			throw new IllegalArgumentException();
		}
		offset += 1;
		// 每次循环都处理一个键值对
		while (offset < str.length())
		{
			c = str.charAt(offset);
			if (c != Symbol.DOUBLE_QUOTATION_MASK.literals())
			{
				throw new IllegalArgumentException();
			}
			String name = getString(str);
			if ((c = str.charAt(offset)) != Symbol.COLON.literals())
			{
				throw new IllegalArgumentException();
			}
			offset += 1;
			c = str.charAt(offset);
			if (c == Symbol.DOUBLE_QUOTATION_MASK.literals())
			{
				String value = getString(str);
				jsonCollection.add(name, value, JsonValueType.STRING);
			}
			else if (c == 'T' || c == 't')
			{
				String value = str.substring(offset, offset + 4);
				if ("TRUE".equals(value) || "true".equals(value))
				{
					jsonCollection.add(name, true, JsonValueType.BOOLEAN);
					offset += 4;
				}
				else
				{
					throw new IllegalArgumentException();
				}
			}
			else if (c == 'F' || c == 'f')
			{
				String value = str.substring(offset, offset + 5);
				if ("false".equals(value) || "FALSE".equals(value))
				{
					jsonCollection.add(name, false, JsonValueType.BOOLEAN);
					offset += 5;
				}
				else
				{
					throw new IllegalArgumentException();
				}
			}
			else if (c >= '0' && c <= '9')
			{
				Number number = getNumber(str);
				if (number instanceof Double)
				{
					jsonCollection.add(name, number, JsonValueType.NUMBER_DOUBLE);
				}
				else
				{
					jsonCollection.add(name, number, JsonValueType.NUMBER_LONG);
				}
			}
			else if (c == Symbol.LEFT_BRACE.literals())
			{
				JsonCollection parseCollection = parseCollection();
				jsonCollection.add(name, parseCollection, JsonValueType.COLLECTION);
				offset += 1;
			}
			else if (c == Symbol.LEFT_BRACKET.literals())
			{
				JsonArray jsonArray = parseArray();
				jsonCollection.add(name, jsonArray, JsonValueType.ARRAY);
				offset += 1;
			}
			else if (c == Symbol.RIGHT_BRACE.literals())
			{
				break;
			}
			else
			{
				throw new IllegalArgumentException();
			}
			c = str.charAt(offset);
			if (c == Symbol.RIGHT_BRACE.literals())
			{
				break;
			}
			if (c != Symbol.COMMA.literals())
			{
				throw new IllegalArgumentException();
			}
			offset += 1;
			continue;
		}
		return jsonCollection;
	}
	
	/**
	 * offset 当前所在位置的内容是'"',处理完成后，offset的位置为结束的'"'位置+1
	 * 
	 * @param str
	 * @return
	 */
	private String getString(String str)
	{
		offset += 1;
		int begin = offset;
		offset += 1;
		while (str.charAt(offset) != Symbol.DOUBLE_QUOTATION_MASK.literals() && offset < str.length())
		{
			offset += 1;
		}
		String result = str.substring(begin, offset);
		offset += 1;
		return result;
	}
	
	/**
	 * 当前位置为数字的起始，结束后位置为最后一位数字的位置+1
	 * 
	 * @param str
	 * @return
	 */
	private Number getNumber(String str)
	{
		int begin = offset;
		offset += 1;
		boolean hasDot = false;
		do
		{
			char c = str.charAt(offset);
			if (c >= '0' && c <= '9')
			{
				offset += 1;
			}
			else if (c == '.')
			{
				offset += 1;
				hasDot = true;
			}
			else
			{
				break;
			}
		} while (offset < str.length());
		if (offset == str.length())
		{
			throw new IllegalArgumentException();
		}
		String value = str.substring(begin, offset);
		Number result = null;
		if (hasDot)
		{
			result = Double.valueOf(value);
		}
		else
		{
			result = Long.valueOf(value);
		}
		return result;
	}
	
}
