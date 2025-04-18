package ar.com.facundoraviolo.versaflake;

import ar.com.facundoraviolo.versaflake.exceptions.InvalidNodeIdException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VersaflakeGeneratorTest {

    @Test
    public void testDefaultIdGeneration() {
        VersaflakeGenerator generator = VersaflakeGenerator.builder(1).build();
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
        VersaflakeGenerator generator = VersaflakeGenerator.builder(1)
                .configuration(config)
                .build();
        long id1 = generator.nextId();
        long id2 = generator.nextId();
        assertNotEquals(id1, id2);
    }

    @Test
    public void testNodeIdOutOfRange() {
        assertThrows(InvalidNodeIdException.class, () -> VersaflakeGenerator.builder(1024).build());
    }

    @Test
    public void testNodeIdInRange() {
        VersaflakeGenerator generator = VersaflakeGenerator.builder(1023).build();
        assertNotNull(generator);
    }

}
