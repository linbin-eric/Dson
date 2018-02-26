package com.jfireframework.dson.deserializer.token;

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
				Element element = new Element(value, JsonValueType.STRING);
				jsonArray.elements.add(element);
			}
			else if (c >= '0' && c <= '9')
			{
				Number number = getNumber(str);
				Element element = new Element(number, JsonValueType.NUMBER);
				jsonArray.elements.add(element);
			}
			else if (c == 'T' || c == 't')
			{
				String value = str.substring(offset, offset + 4);
				if ("TRUE".equals(value) || "true".equals(value))
				{
					Element element = new Element(true, JsonValueType.BOOLEAN);
					jsonArray.elements.add(element);
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
					Element element = new Element(false, JsonValueType.BOOLEAN);
					jsonArray.elements.add(element);
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
				Element element = new Element(parseCollection, JsonValueType.COLLECTION);
				jsonArray.elements.add(element);
				offset += 1;
			}
			else if (c == Symbol.LEFT_BRACKET.literals())
			{
				JsonArray array = parseArray();
				Element element = new Element(array, JsonValueType.ARRAY);
				jsonArray.elements.add(element);
				offset += 1;
			}
			else
			{
				throw new IllegalArgumentException();
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
				Entry entry = new Entry(name, value, JsonValueType.STRING);
				jsonCollection.entries.add(entry);
			}
			else if (c == 'T' || c == 't')
			{
				String value = str.substring(offset, offset + 4);
				if ("TRUE".equals(value) || "true".equals(value))
				{
					Entry entry = new Entry(name, true, JsonValueType.BOOLEAN);
					jsonCollection.entries.add(entry);
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
					Entry entry = new Entry(name, false, JsonValueType.BOOLEAN);
					jsonCollection.entries.add(entry);
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
				Entry entry = new Entry(name, number, JsonValueType.NUMBER);
				jsonCollection.entries.add(entry);
			}
			else if (c == Symbol.LEFT_BRACE.literals())
			{
				JsonCollection parseCollection = parseCollection();
				Entry entry = new Entry(name, parseCollection, JsonValueType.COLLECTION);
				jsonCollection.entries.add(entry);
				offset += 1;
			}
			else if (c == Symbol.LEFT_BRACKET.literals())
			{
				JsonArray jsonArray = parseArray();
				Entry entry = new Entry(name, jsonArray, JsonValueType.ARRAY);
				jsonCollection.entries.add(entry);
				offset += 1;
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
