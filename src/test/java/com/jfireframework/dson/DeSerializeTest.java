package com.jfireframework.dson;

import org.junit.Test;
import com.jfireframework.dson.metadata.json.JsonCollection;
import com.jfireframework.dson.metadata.parse.Lexer;

public class DeSerializeTest
{
	String value = "{\"name\" :   \"sad\",\"age\":12,\"sex\":false,\"home\":{\"leng\":12.156},\"address\":[\"上海\",\"福州\"]}";
	
	@Test
	public void test()
	{
		Lexer lexer = new Lexer(value);
		JsonCollection parse = (JsonCollection) lexer.parse();
		System.out.println(parse);
	}
	
}
