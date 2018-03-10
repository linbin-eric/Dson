package com.jfireframework.dson.util;

public class StringBuilderAdaptStringOutput implements StringOutput
{
	private StringBuilder builder = new StringBuilder();
	
	@Override
	public StringOutput append(String content)
	{
		builder.append(content);
		return this;
	}
	
	@Override
	public StringOutput append(char content)
	{
		builder.append(content);
		return this;
	}
	
	@Override
	public StringOutput append(int content)
	{
		builder.append(content);
		return this;
	}
	
	@Override
	public StringOutput append(byte content)
	{
		builder.append(content);
		return this;
	}
	
	@Override
	public StringOutput append(short content)
	{
		builder.append(content);
		return this;
	}
	
	@Override
	public StringOutput append(long content)
	{
		builder.append(content);
		return this;
	}
	
	@Override
	public StringOutput append(float content)
	{
		builder.append(content);
		return this;
	}
	
	@Override
	public StringOutput append(double content)
	{
		builder.append(content);
		return this;
	}
	
	@Override
	public StringOutput append(boolean content)
	{
		builder.append(content);
		return this;
	}
	
	@Override
	public StringOutput append(Object content)
	{
		builder.append(content);
		return this;
	}
	
	@Override
	public StringOutput appendDoubleQuotes()
	{
		builder.append('"');
		return this;
	}
	
	@Override
	public StringOutput deleteLast()
	{
		builder.deleteCharAt(builder.length() - 1);
		return this;
	}
	
	@Override
	public int length()
	{
		return builder.length();
	}
	
	@Override
	public StringOutput clear()
	{
		builder.delete(0, builder.length());
		return this;
	}
	
	public String toString()
	{
		return builder.toString();
	}
}
