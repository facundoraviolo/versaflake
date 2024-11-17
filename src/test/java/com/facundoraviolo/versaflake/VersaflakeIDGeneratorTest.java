package com.facundoraviolo.versaflake;

import com.facundoraviolo.versaflake.exceptions.InvalidNodeIdException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VersaflakeIDGeneratorTest {

    @Test
    public void testDefaultIdGeneration() {
        VersaflakeIDGenerator generator = new VersaflakeIDGenerator.Builder(1).build();
        long id1 = generator.nextId();
        long id2 = generator.nextId();
        assertNotEquals(id1, id2);
    }

    @Test
    public void testCustomIdGeneration() {
        VersaflakeConfiguration config = new VersaflakeConfiguration.Builder()
                .startEpoch(1609459200000L)
                .nodeIdBits(12)
                .sequenceBits(10)
                .build();
        VersaflakeIDGenerator generator = new VersaflakeIDGenerator.Builder(1)
                .configuration(config)
                .build();
        long id1 = generator.nextId();
        long id2 = generator.nextId();
        assertNotEquals(id1, id2);
    }

    @Test
    public void testNodeIdOutOfRange() {
        assertThrows(InvalidNodeIdException.class, () -> new VersaflakeIDGenerator.Builder(1024).build());
    }

    @Test
    public void testNodeIdInRange() {
        VersaflakeIDGenerator generator = new VersaflakeIDGenerator.Builder(1023).build();
        assertNotNull(generator);
    }

}
