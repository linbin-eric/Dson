package com.jfirer.dson.benchmark.read;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfirer.dson.Dson;
import com.jfirer.dson.NestData;
import com.jfirer.dson.benchmark.BigData;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class BenchmarkBig
{
    @State(Scope.Benchmark)
    public static class ForTestData
    {
        BigData bigData;
        String  content;
        ObjectMapper mapper = new ObjectMapper();

        @Setup
        public void set()
        {
            bigData = new BigData();
            bigData.setA(12);
            bigData.setB(2.36f);
            bigData.setC(5.6987);
            bigData.setD(121212121212l);
            bigData.setE('f');
            bigData.setF(true);
            bigData.setG((short) 5.689);
            bigData.setH((byte) 3);
            bigData.setA1(1);
            bigData.setB1(2.34f);
            bigData.setC1(2323.34234234);
            bigData.setD1(11231231231313133l);
            bigData.setE1("sdasdasd");
            bigData.setF1(true);
            bigData.setG1((short) 2);
            bigData.setH1((byte) 12);
            NestData nestData = new NestData();
            nestData.setName("dsadas");
            bigData.setNestData(nestData);
            ArrayList<String> list = new ArrayList<String>();
            list.add("husdasdad");
            list.add("siudsan");
            bigData.setList(list);
            ArrayList<NestData> nestDatas = new ArrayList<NestData>();
            nestData = new NestData();
            nestData.setName("sdasda");
            nestData.setAge(13);
            nestDatas.add(nestData);
            nestData = new NestData();
            nestData.setName("dasdas");
            nestData.setAge(20);
            nestDatas.add(nestData);
            bigData.setDatas(nestDatas);
            bigData.setNolist(new ArrayList<String>());
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("恁大", "dasdasd");
            map.put("dsada", "你好");
            bigData.setMap(map);
            bigData.setArray2(new int[][]{{1, 2, 3, 4}, {10, 12}});
            bigData.setStrs(new String[]{"231231", "sdadsasdasd"});
            bigData.setArray1(new int[]{1, 2, 3, 4, 5, 65});
            bigData.setChars(new char[]{'a', 'b'});
            bigData.setArray3(new Integer[]{1, 2, 3, 4, 5, 6, 7});
            bigData.setArray4(new Integer[][]{{1, 2, 3, 4, 5, 56}, {10, 11, 12, 14}});
            NestData[] nestDatas2 = new NestData[2];
            NestData   tmp        = new NestData();
            tmp.setAge(12);
            tmp.setName("das");
            nestDatas2[0] = tmp;
            tmp = new NestData();
            tmp.setAge(1222);
            tmp.setName("daasdadasd");
            nestDatas2[1] = tmp;
            bigData.setNestDatas(nestDatas2);
            HashMap<Date, NestData> map2 = new HashMap<Date, NestData>();
            map2.put(new Date(), tmp);
            @SuppressWarnings("unchecked") ArrayList<String>[] lists = new ArrayList[]{new ArrayList<String>(), new ArrayList<String>()};
            lists[0].add("dasdasda");
            lists[0].add("dasdasdasdasdasd");
            lists[1].add("1212121dasdasdasdasdasd");
            lists[1].add("dasdasd1212121212asdasdasd");
            bigData.setLists(lists);
            bigData.setData(nestDatas2);
            content = Dson.toJson(bigData);
        }
    }

    @Benchmark
    public void testFastJson(ForTestData forTestData, Blackhole blackhole)
    {
        BigData bigData = JSON.parseObject(forTestData.content, BigData.class);
        blackhole.consume(bigData);
    }

    @Benchmark
    public void testJackson(ForTestData forTestData, Blackhole blackhole)
    {
        try
        {
            BigData bigData = forTestData.mapper.readValue(forTestData.content, BigData.class);
            blackhole.consume(bigData);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Benchmark
    public void testNew(ForTestData forTestData,Blackhole blackhole){
        Object o = Dson.fromString(BigData.class, forTestData.content);
        blackhole.consume(o);
    }

    @Benchmark
    public void testCompile(ForTestData forTestData, Blackhole blackhole)
    {
        Object o = Dson.fromStringByCompile(BigData.class, forTestData.content);
        blackhole.consume(o);
    }
    public static void main(String[] args) throws RunnerException
    {
        Options opt = new OptionsBuilder().include(BenchmarkBig.class.getSimpleName())//
                .warmupIterations(2).warmupTime(TimeValue.seconds(2))//
                .measurementIterations(3).forks(1).measurementTime(TimeValue.seconds(2))//
                .threads(1).forks(1).build();
        new Runner(opt).run();
    }
}
