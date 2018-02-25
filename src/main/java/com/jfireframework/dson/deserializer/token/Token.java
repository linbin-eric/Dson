package com.jfireframework.dson.deserializer.token;

public class Token
{
	private String		literals;
	private TokenType	type;
	private int			endPosition;
	
	public Token(String literals, TokenType type, int endPosition)
	{
		this.literals = literals;
		this.type = type;
		this.endPosition = endPosition;
	}
	
	public String getLiterals()
	{
		return literals;
	}
	
	public void setLiterals(String literals)
	{
		this.literals = literals;
	}
	
	public TokenType getType()
	{
		return type;
	}
	
	public void setType(TokenType type)
	{
		this.type = type;
	}
	
	public int getEndPosition()
	{
		return endPosition;
	}
	
	public void setEndPosition(int endPosition)
	{
		this.endPosition = endPosition;
	}
	
}
