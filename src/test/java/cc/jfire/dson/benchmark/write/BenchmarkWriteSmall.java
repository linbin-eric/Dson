package cc.jfire.dson.benchmark.write;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cc.jfire.dson.Dson;
import cc.jfire.dson.benchmark.SmallObject;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 3, time = 3)
@Fork(1)
public class BenchmarkWriteSmall
{
    @State(Scope.Benchmark)
    public static class Fortest
    {
        SmallObject  smallData;
        ObjectMapper mapper = new ObjectMapper();

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
        String s = Dson.toJson(fortest.smallData);
        blackhole.consume(s);
    }

    @Benchmark
    public String testDsonCompile(Fortest fortest)
    {
        String s = Dson.toJsonByCompile(fortest.smallData);
        return s;
    }

    public static void main(String[] args) throws RunnerException
    {
        Options opt = new OptionsBuilder().include(BenchmarkWriteSmall.class.getSimpleName())//
                                          .build();
        new Runner(opt).run();
    }
}
