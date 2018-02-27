package com.jfireframework.dson;

import org.junit.Ignore;
import org.junit.Test;
import com.jfireframework.baseutil.time.Timewatch;
import com.jfireframework.codejson.JsonTool;
import com.jfireframework.dson.deserializer.token.Lexer;
import com.jfireframework.dson.metadata.json.JsonCollection;

public class DeSerializeTest
{
	String value = "{\"name\":\"sad\",\"age\":12,\"sex\":false,\"home\":{\"leng\":12.156},\"address\":[\"上海\",\"福州\"]}";
	
	@Test
	public void test()
	{
		Lexer lexer = new Lexer(value);
		JsonCollection parse = (JsonCollection) lexer.parse();
		System.out.println(parse);
	}
	
	@Test
	public void speedTest()
	{
		for (int i = 0; i < 100; i++)
		{
			JsonTool.fromString(value);
			Lexer lexer = new Lexer(value);
			lexer.parse();
		}
		int count = 10000000;
		Timewatch timewatch = new Timewatch();
		timewatch.start();
		for (int i = 0; i < count; i++)
		{
			new Lexer(value).parse();
		}
		timewatch.end();
		System.out.println("    dson:" + timewatch.getTotal());
		timewatch.start();
		for (int i = 0; i < count; i++)
		{
			JsonTool.fromString(value);
		}
		timewatch.end();
		System.out.println("codejson:" + timewatch.getTotal());
	}
}
