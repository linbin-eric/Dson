package com.jfireframework.dson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Ignore;
import org.junit.Test;
import com.jfireframework.baseutil.reflect.TypeUtil;
import com.jfireframework.codejson.util.NameTool;
import com.jfireframework.dson.model.FunData16;
import com.jfireframework.dson.model.FunctionData10;
import com.jfireframework.dson.model.FunctionData13;
import com.jfireframework.dson.model.FunctionData16;
import com.jfireframework.dson.model.FunctionData7;
import com.jfireframework.dson.model.TestEnum;

public class FunctionTest extends Support
{
	
	@Test
	public void rightTest()
	{
		String string = Dson.toJsonString(data);
		logger.debug("输出的json是\n\n{}\r\n\n", string);
		assertTrue(data.equal(Dson.fromString(Data.class, string)));
		logger.debug("输出的数组json是\n\n{}\r\n\n", Dson.toJsonString(new Data[] { data, data }));
		Data[][] origin = new Data[][] { { data, data }, { data, data, data } };
		string = Dson.toJsonString(origin);
		Data[][] result = Dson.fromString(Data[][].class, string);
		for (int i = 0; i < origin.length; i++)
		{
			for (int j = 0; j < origin[i].length; j++)
			{
				assertTrue(origin[i][j].equal(result[i][j]));
			}
		}
		Data[] test1 = new Data[] { data, data };
		string = Dson.toJsonString(test1);
		Data[] test1Result = Dson.fromString(Data[].class, string);
		for (int i = 0; i < test1.length; i++)
		{
			assertTrue(test1[i].equal(test1Result[i]));
		}
		Data[][][] test2 = new Data[][][] { { { data, data }, { data } }, { { data, data, data, data }, { data }, { data } } };
		string = Dson.toJsonString(test2);
		Data[][][] test2Result = Dson.fromString(Data[][][].class, string);
		for (int i = 0; i < test2Result.length; i++)
		{
			for (int j = 0; j < test2Result[i].length; j++)
			{
				for (int k = 0; k < test2Result[i][j].length; k++)
				{
					assertTrue(test2Result[i][j][k].equal(test2[i][j][k]));
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void typeTest()
	{
		ArrayList<String> list = new ArrayList<String>();
		list.add("sdadasd");
		list.add("sdadsasda");
		String value = Dson.toJsonString(list);
		ArrayList<String> result = (ArrayList<String>) Dson.fromString(new TypeUtil<ArrayList<String>>() {}.getType(), value);
		assertTrue(list.equals(result));
		ArrayList<NestData> arrayList = new ArrayList<NestData>();
		NestData cdata = new NestData();
		cdata.setAge(1212);
		cdata.setName("sdasdas");
		arrayList.add(cdata);
		cdata = new NestData();
		cdata.setAge(1212121);
		cdata.setName("dassdas");
		arrayList.add(cdata);
		value = Dson.toJsonString(arrayList);
		System.out.println(value);
		ArrayList<NestData> result1 = Dson.fromString(new TypeUtil<ArrayList<NestData>>() {}.getType(), value);
		assertTrue(arrayList.equals(result1));
		ArrayList<Data> list2 = new ArrayList<Data>();
		list2.add(data);
		list2.add(data);
		value = Dson.toJsonString(list2);
		System.out.println(value);
		ArrayList<Data> result3 = Dson.fromString(new TypeUtil<ArrayList<Data>>() {}.getType(), value);
		assertTrue(list2.get(0).equal(result3.get(0)) && list2.get(1).equal(result3.get(1)));
		HashMap<String, Data> map = new HashMap<String, Data>();
		map.put("12wq", data);
		map.put("xczc", data);
		value = Dson.toJsonString(map);
		HashMap<String, Data> result4 = Dson.fromString(new TypeUtil<HashMap<String, Data>>() {}.getType(), value);
		assertTrue(map.get("12wq").equal(result4.get("12wq")));
		assertTrue(map.get("xczc").equal(result4.get("xczc")));
	}
	
	@Test
	public void test1()
	{
		assertEquals("{\"data\":{\"1\":\"121212\"}}", Dson.toJsonString(new FunctionData7()));
	}
	
	@Test
	public void test2()
	{
		new NameTool();
		System.out.println(Dson.toJsonString(new FunctionData10()));
	}
	
	@Test
	@Ignore
	public void test3()
	{
		System.out.println(Dson.toJsonString(new FunctionData13()));
		assertEquals("{\"array1\":[\"1212\",\"12112\"],\"array2\":[1,2,3],\"array3\":[true,false],\"array4\":[\"c\",\"d\"],\"array5\":[1,2,3,4,5,7],\"array6\":[1221121231231,212312313],\"array7\":[2.36,5.698],\"array8\":[2323.231,2323.2313123],\"array9\":[100,23]}", Dson.toJsonString(new FunctionData13()));
	}
	
	@Test
	public void enumTest()
	{
		FunData16 data16 = new FunData16();
		data16.setTest(TestEnum.PUSH);
		assertEquals("{\"test\":\"PUSH\"}", Dson.toJsonString(data16));
	}
	
	@Test
	public void definitionTest()
	{
		FunctionData16 data16 = new FunctionData16();
		data16.setName("111");
		assertEquals("{\"name\":\"123\"}", Dson.toJsonString(data16));
	}
}
