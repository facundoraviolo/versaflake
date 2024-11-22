package ar.com.facundoraviolo.versaflake;

import ar.com.facundoraviolo.versaflake.exceptions.InvalidNodeIdException;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Test
    void testWaitForNextMillisIsExecuted() {
        long nodeId = 1;
        long startEpoch = 1704067200000L;
        long nodeIdBits = 18;
        long sequenceBits = 4;
        Clock mockClock = mock(Clock.class);

        VersaflakeGenerator generator = new VersaflakeGenerator(mockClock, nodeId, startEpoch, nodeIdBits, sequenceBits);

        when(mockClock.millis()).thenReturn(startEpoch);
        for (int i = 0; i <= ~(-1L << sequenceBits); i++) {
            generator.nextId();
        }

        when(mockClock.millis()).thenReturn(startEpoch + 1);
        long id = generator.nextId();

        assertEquals(4194320, id);
    }

}
