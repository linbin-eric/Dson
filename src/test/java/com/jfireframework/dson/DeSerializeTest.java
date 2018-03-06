package com.jfireframework.dson;

import org.junit.Test;
import com.jfireframework.dson.metadata.json.JsonCollection;
import com.jfireframework.dson.metadata.parse.Lexer;

public class DeSerializeTest
{
	String	value	= "{\"name\" :   \"sad\",\"age\":12,\"sex\":false,\"home\":{\"leng\":12.156},\"address\":[\"上海\",\"福州\"]}";
	String	value1	= "{\r\n" + "	\"qrcode\": \"https://qr.95516.com/00010000/62219770209542947931402889419177\",\r\n" + "	\"userPlatAuthCode\": \"MTY3ODA3MTUyOTE1NyxjaXRpemVucGF5X3Rlc3RfMDEsQjlGNUYzRjFEOUQ4NDQ5RTk5MUM5MUJFRDFCQUY4MjEseWh6U0lYZnd4N0pIL3plM3huOFFCaXRrazF5clQycDVNZVFpcTNLSXR3ST0=\"\r\n" + "}";
	
	@Test
	public void test()
	{
		Lexer lexer = new Lexer(value);
		JsonCollection parse = (JsonCollection) lexer.parse();
		System.out.println(parse);
	}
	
	@Test
	public void test2()
	{
		Lexer lexer = new Lexer(value1);
		lexer.parse();
	}
	
}
