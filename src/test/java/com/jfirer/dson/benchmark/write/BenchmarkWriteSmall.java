package com.jfirer.dson.benchmark.write;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfirer.dson.benchmark.SmallObject;
import com.jfirer.dson.writer.JsonWriter;
import com.jfirer.dson.writer.TypeWriter;
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

public class BenchmarkWriteSmall
{
    @State(Scope.Benchmark)
    public static class Fortest
    {
        SmallObject   smallData;
        ObjectMapper  mapper     = new ObjectMapper();
        JsonWriter    jsonWriter = new JsonWriter();
        StringBuilder builder    = new StringBuilder();

        @Setup
        public void set()
        {
            smallData = new SmallObject();
            smallData.setA(1);
            smallData.setA1(12);
            smallData.setAge(12);
            smallData.setB(5.6f);
            smallData.setB1(2.36f);
            smallData.setC(2.3659);
            smallData.setC1(2.3656);
            smallData.setD(56676416847694l);
            smallData.setD1(12312312l);
            smallData.setE('e');
            smallData.setE1("2ewaedasdas");
            smallData.setF(true);
        }
    }

    @Benchmark
    public void testFastjson(Fortest fortest, Blackhole blackhole)
    {
        String s = JSON.toJSONString(fortest.smallData);
        blackhole.consume(s);
    }

    @Benchmark
    public void testJackSon(Fortest fortest, Blackhole blackhole)
    {
        try
        {
            String s = fortest.mapper.writeValueAsString(fortest.smallData);
            blackhole.consume(s);
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }
    }

    @Benchmark
    public void testDson(Fortest fortest, Blackhole blackhole)
    {
        JsonWriter    jsonWriter = fortest.jsonWriter;
        StringBuilder builder    = fortest.builder;
        SmallObject   smallData  = fortest.smallData;
        TypeWriter    typeWriter = jsonWriter.get(smallData.getClass());
        typeWriter.toJson(smallData, builder);
        String s1 = builder.toString();
        builder.setLength(0);
        blackhole.consume(s1);
    }

    public static void main(String[] args) throws RunnerException
    {
        Options opt = new OptionsBuilder().include(BenchmarkWriteSmall.class.getSimpleName())//
                .warmupIterations(3).warmupTime(TimeValue.seconds(3))//
                .measurementIterations(3).forks(1).measurementTime(TimeValue.seconds(3))//
                .build();
        new Runner(opt).run();
    }
}
