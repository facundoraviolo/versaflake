package ar.com.facundoraviolo.versaflake;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class VersaflakeBenchmark {

    private VersaflakeGenerator generator;

    @Setup(Level.Iteration)
    public void setup() {
        generator = VersaflakeGenerator.builder(1).build();
    }

    @Benchmark
    public long generateId() {
        return generator.nextId();
    }

}
