package ar.com.facundoraviolo.versaflake;

import ar.com.facundoraviolo.versaflake.exceptions.InvalidNodeIdException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VersaflakeIDGeneratorTest {

    @Test
    public void testDefaultIdGeneration() {
        VersaflakeIDGenerator generator = VersaflakeIDGenerator.builder(1).build();
        long id1 = generator.nextId();
        long id2 = generator.nextId();
        assertNotEquals(id1, id2);
    }

    @Test
    public void testCustomIdGeneration() {
        VersaflakeConfiguration config = VersaflakeConfiguration.builder()
                .startEpoch(1609459200000L)
                .nodeIdBits(12)
                .sequenceBits(10)
                .build();
        VersaflakeIDGenerator generator = VersaflakeIDGenerator.builder(1)
                .configuration(config)
                .build();
        long id1 = generator.nextId();
        long id2 = generator.nextId();
        assertNotEquals(id1, id2);
    }

    @Test
    public void testNodeIdOutOfRange() {
        assertThrows(InvalidNodeIdException.class, () -> VersaflakeIDGenerator.builder(1024).build());
    }

    @Test
    public void testNodeIdInRange() {
        VersaflakeIDGenerator generator = VersaflakeIDGenerator.builder(1023).build();
        assertNotNull(generator);
    }

}
