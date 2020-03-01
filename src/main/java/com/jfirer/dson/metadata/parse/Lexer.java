package com.jfirer.dson.metadata.parse;

import com.jfirer.dson.metadata.json.DsonObject;
import com.jfirer.dson.metadata.json.JsonArray;
import com.jfirer.dson.metadata.json.JsonCollection;
import com.jfirer.dson.metadata.json.JsonValueType;

public class Lexer
{
	
	private int										offset	= 0;
	private String									str;
	private int										length;
	private static final ThreadLocal<StringBuilder>	LOCAL	= new ThreadLocal<StringBuilder>() {
																protected StringBuilder initialValue()
																{
																	return new StringBuilder();
																};
															};
	private StringBuilder								cache;
	
	public Lexer(String str)
	{
		this.str = str;
		length = str.length();
		cache = LOCAL.get();
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
		while (offset < length)
		{
			c = ignoreSymbol();
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
			else if (c == 't' || c == 'T')
			{
				String value = str.substring(offset, offset + 4);
				if ("TRUE".equals(value) || "true".equals(value))
				{
					jsonArray.add(true, JsonValueType.BOOLEAN);
					offset += 4;
				}
				else
				{
					throw new IllegalArgumentException("无法识别的json内容：" + value);
				}
			}
			else if (c == 'f' || c == 'F')
			{
				String value = str.substring(offset, offset + 5);
				if ("false".equals(value) || "FALSE".equals(value))
				{
					jsonArray.add(false, JsonValueType.BOOLEAN);
					offset += 5;
				}
				else
				{
					throw new IllegalArgumentException("无法识别的json内容：" + value);
				}
			}
			else if (c == 'N' || c == 'n')
			{
				String value = str.substring(offset, offset + 4);
				if ("null".equals(value) || "NULL".equals(value))
				{
					jsonArray.add(null, JsonValueType.NULL);
					offset += 4;
				}
				else
				{
					throw new IllegalArgumentException("无法识别的json内容：" + value);
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
			c = ignoreSymbol();
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
		while (offset < length)
		{
			c = ignoreSymbol();
			if (c != Symbol.DOUBLE_QUOTATION_MASK.literals())
			{
				throw new IllegalArgumentException();
			}
			String name = getString(str);
			if ((c = ignoreSymbol()) != Symbol.COLON.literals())
			{
				throw new IllegalArgumentException();
			}
			offset += 1;
			c = ignoreSymbol();
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
			else if (c == 'n' || c == 'N')
			{
				String value = str.substring(offset, offset + 4);
				if ("null".equals(value) || "NULL".equals(value))
				{
					jsonCollection.add(name, null, JsonValueType.NULL);
					offset += 4;
				}
				else
				{
					throw new IllegalArgumentException();
				}
			}
			else if ((c >= '0' && c <= '9') || c == '-')
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
				throw new IllegalArgumentException("非法字符:" + c + ",当前解析进度:" + str.substring(offset));
			}
			c = ignoreSymbol();
			if (c == Symbol.RIGHT_BRACE.literals())
			{
				break;
			}
			if (c != Symbol.COMMA.literals())
			{
				throw new IllegalArgumentException("非法字符:" + c + ",当前解析进度:" + str.substring(offset));
			}
			offset += 1;
			continue;
		}
		return jsonCollection;
	}
	
	private char ignoreSymbol()
	{
		char c = str.charAt(offset);
		do
		{
			if (c == Symbol.BLANK.literals() //
			        || c == Symbol.RETURN.literals()//
			        || c == Symbol.NEWLINE.literals()//
			        || c == Symbol.TAB.literals())
			{
				offset += 1;
				c = str.charAt(offset);
			}
			else
			{
				return c;
			}
		} while (offset < length);
		return c;
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
		char c;
		cache.setLength(0);
		do
		{
			c = str.charAt(offset);
			if (c == '\\')
			{
				if (offset++ < length)
				{
					if ((c = str.charAt(offset)) == '"')
					{
						cache.append('"');
					}
					else
					{
						cache.append('\\').append(c);
					}
					offset += 1;
				}
				else
				{
					cache.append(c);
				}
			}
			else if (c == Symbol.DOUBLE_QUOTATION_MASK.literals())
			{
				break;
			}
			else
			{
				cache.append(c);
				offset += 1;
			}
		} while (offset < length);
		// String result = str.substring(begin, offset);
		offset += 1;
		// return result;
		return cache.toString();
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
