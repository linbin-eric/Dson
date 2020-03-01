package com.jfirer.dson;

import com.jfirer.dson.metadata.json.JsonCollection;
import com.jfirer.dson.metadata.parse.Lexer;
import org.junit.Test;

public class DeSerializeTest
{
	String	value	= "{\"name\" :   \"sad\",\"age\":12,\"sex\":false,\"home\":{\"leng\":12.156},\"address\":[\"上海\",\"福州\"]}";
	String	value1	= "{\r\n" + "	\"qrcode\": \"\\\"https://qr.95516.com/00010000/62219770209542947931402889419177\",\r\n" + "	\"userPlatAuthCode\": \"MTY3ODA3MTUyOTE1NyxjaXRpemVucGF5X3Rlc3RfMDEsQjlGNUYzRjFEOUQ4NDQ5RTk5MUM5MUJFRDFCQUY4MjEseWh6U0lYZnd4N0pIL3plM3huOFFCaXRrazF5clQycDVNZVFpcTNLSXR3ST0=\"\r\n" + "}";
	String	value2	= "{\r\n" + "	\"payOrderNo\": \"DD1520307533990\",\r\n" + "	\"orderDate\": \"20180306\",\r\n" + "	\"orderTime\": \"113855\",\r\n" + "	\"currencyCode\": \"01\",\r\n" + "	\"amt\": -1,\r\n" + "	\"orderBrief\": \"医疗机构收费\",\r\n" + "	\"orderTitle\": \"医疗机构发起收款0.01元\",\r\n" + "	\"orderDesc\": \"医疗机构发起收款0.01元\",\r\n" + "	\"orderTemplate\": \"\",\r\n" + "	\"notifyUrl\": \"http://testbs.ggjfw.com/mockMerchantUtil/api/notify\",\r\n" + "	\"qrcode\": \"AQL3/WdpRAi08d+MH5mZCteLxUbnqQEV4xJY02Zdm7pHxnzCEgtkcQBruz7LD7AK57WqA6mgZ4sVRRdT/JU82fP9AAECN3NfAGW2uisYDHq14a1bfdNmA6fWmcJVZbwWV8xA38p2cBEOGtF6kgIpyjDqo5TLWsRkJQ7tTuaD/3CDGU1h8mhbxhxQtRwqKHivTlu/D2fH6Di7wwI2NwQCNjdAAAAK0/QU0oAAAP//AAAAAAAAAAAAAAAAAAA\r\n" + "AAOYETIC1UUDTubYHCluh0NskH0V6DM5LaPaGojG95W96fk0400zQXyL+GooDnx+9yuL+r7XSWYJNi/4E2SYevK8qQohPwEaVzASBJqzut808\",	\"returnPara\": \"\",\r\n" + "	\"type\": \"99\",\r\n" + "	\"govPayNo\": \"\",\r\n" + "	\"merchantId\": \"201705240001\"\r\n" + "}";
	String	value3	= "{\r\n" + "	\"userPlatAuthCode\": \"\\\"MTY3ODA4NzAzNDcxNSxjaXRpemVucGF5X3Rlc3RfMDEsQTdDOEY4NjdGNEU5NEQxMkE5OTY0OUMzQUEwMzkzMUEsdjlJWTJRbDg5d1psME9TQmwvNUtrVWR0eFRYYnppdmhtWmpQMURoajZRRT0=\"\r\n" + "}";
	
	@Test
	public void test()
	{
		Lexer          lexer = new Lexer(value);
		JsonCollection parse = (JsonCollection) lexer.parse();
		System.out.println(parse);
	}
	
	@Test
	public void test1()
	{
		Lexer lexer = new Lexer(value1);
		lexer.parse();
	}
	
	@Test
	public void test2()
	{
		Lexer lexer = new Lexer(value2);
		lexer.parse();
	}
	
	@Test
	public void test3()
	{
		new Lexer(value3).parse();
	}
}
