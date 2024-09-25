package com.jfirer.dson.benchmark.read;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfirer.dson.DsonConfig;
import com.jfirer.dson.DsonContext;
import com.jfirer.dson.benchmark.SmallObject;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 2, time = 2)
@Measurement(iterations = 3, time = 3)
@Fork(2)
@State(Scope.Benchmark)
public class BenchmarkSmall
{
    DsonContext standard = new DsonContext();
    DsonContext compile  = new DsonContext(new DsonConfig().setReadUseCompile(true));
    DsonContext compile2 = new DsonContext(new DsonConfig().setReadEntryUseCompile(true));
    DsonContext compile3 = new DsonContext(new DsonConfig().setValueAccessorUseCompile(true));

    @State(Scope.Benchmark)
    public static class SmallData
    {
        private SmallObject smallData;
        String       value;
        ObjectMapper mapper;

        @Setup(Level.Trial)
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
            value  = """
                    {"s":0,"a1":12,"a":1,"age":12,"b":5.6,"b1":2.36,"c":2.3659,"c1":2.3656,"d":56676416847694,"d1":12312312,"e":"e","e1":"2ewaedasdas","f":true,"g":0,"h":0}
                    """;
        }
    }

    @Benchmark
    public void testStandard(Blackhole blackhole, SmallData smallData)
    {
        Object o = standard.fromString(SmallObject.class, smallData.value);
        blackhole.consume(o);
    }

    @Benchmark
    public void testCompile1(Blackhole blackhole, SmallData smallData)
    {
        Object o = compile.fromString(SmallObject.class, smallData.value);
        blackhole.consume(o);
    }

    @Benchmark
    public void testCompile2(Blackhole blackhole, SmallData smallData)
    {
        Object o = compile2.fromString(SmallObject.class, smallData.value);
        blackhole.consume(o);
    }

    @Benchmark
    public void testCompile3(Blackhole blackhole, SmallData smallData)
    {
        Object o = compile3.fromString(SmallObject.class, smallData.value);
        blackhole.consume(o);
    }

    @Benchmark
    public void testFastJson(Blackhole blackhole, SmallData smallData)
    {
        SmallObject smallObject = JSON.parseObject(smallData.value, SmallObject.class);
        blackhole.consume(smallObject);
    }

    @Benchmark
    public void testJackson(Blackhole blackhole, SmallData smallData)
    {
        try
        {
            SmallObject smallObject = smallData.mapper.readValue(smallData.value, SmallObject.class);
            blackhole.consume(smallObject);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws RunnerException
    {
        Options opt = new OptionsBuilder().include(BenchmarkSmall.class.getSimpleName()).build();
        new Runner(opt).run();
    }
}
