package com.jfireframework.dson.util;

import com.jfireframework.baseutil.collection.StringCache;

public class StringCacheAdaptStringOutput implements StringOutput
{
	private StringCache cache = new StringCache();
	
	@Override
	public StringOutput append(String content)
	{
		cache.append(content);
		return this;
	}
	
	@Override
	public StringOutput append(char content)
	{
		cache.append(content);
		return this;
	}
	
	@Override
	public StringOutput append(int content)
	{
		cache.append(content);
		return this;
	}
	
	@Override
	public StringOutput append(byte content)
	{
		cache.append(content);
		return this;
	}
	
	@Override
	public StringOutput append(short content)
	{
		cache.append(content);
		return this;
	}
	
	@Override
	public StringOutput append(long content)
	{
		cache.append(content);
		return this;
	}
	
	@Override
	public StringOutput append(float content)
	{
		cache.append(content);
		return this;
	}
	
	@Override
	public StringOutput append(double content)
	{
		cache.append(content);
		return this;
	}
	
	@Override
	public StringOutput append(boolean content)
	{
		cache.append(content);
		return this;
	}
	
	@Override
	public StringOutput append(Object content)
	{
		cache.append(content);
		return this;
	}
	
	@Override
	public StringOutput appendDoubleQuotes()
	{
		cache.append('"');
		return this;
	}
	
	@Override
	public StringOutput deleteLast()
	{
		cache.deleteLast();
		return this;
	}
	
	@Override
	public int length()
	{
		return cache.count();
	}
	
	public String toString()
	{
		return cache.toString();
	}
}
