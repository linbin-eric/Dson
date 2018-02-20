package com.jfireframework.dson.util;

public interface StringOutput
{
	StringOutput append(String content);
	
	StringOutput append(char content);
	
	StringOutput append(int content);
	
	StringOutput append(byte content);
	
	StringOutput append(short content);
	
	StringOutput append(long content);
	
	StringOutput append(float content);
	
	StringOutput append(double content);
	
	StringOutput append(boolean content);
	
	StringOutput append(Object content);
	
	/**
	 * 添加一个双引号'"'
	 * 
	 * @return
	 */
	StringOutput appendDoubleQuotes();
	
	StringOutput deleteLast();
	
	int length();
	
	String toString();
	
	StringOutput clear();
}
