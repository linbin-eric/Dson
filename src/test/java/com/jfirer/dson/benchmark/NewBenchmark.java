package com.jfirer.dson.benchmark;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfirer.baseutil.time.Timewatch;
import com.jfirer.dson.Dson;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.io.IOException;

@State(Scope.Benchmark)
public class NewBenchmark
{
    private SmallObject smallData;
    String value;
    ObjectMapper mapper ;
    @Setup
    public void before()
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
        mapper = new ObjectMapper();
        value = Dson.toJsonString(smallData);
    }

    @Benchmark
    public void testOld()
    {
        Dson.fromString(SmallObject.class, value);
    }

    @Benchmark
    public void testNew()
    {
        Dson.fromString2(SmallObject.class, value);
    }
    @Benchmark
    public void testFastJson(){
        JSON.parseObject(value, SmallObject.class);
    }

    @Benchmark
    public void testJackson(){
        try
        {
            mapper.readValue(value, SmallObject.class);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws RunnerException
    {
        Options opt = new OptionsBuilder().include(NewBenchmark.class.getSimpleName()).warmupIterations(2).warmupTime(TimeValue.seconds(3))
                .measurementIterations(3)
                .forks(1)
                .measurementTime(TimeValue.seconds(3)).build();
        new Runner(opt).run();
    }
}
