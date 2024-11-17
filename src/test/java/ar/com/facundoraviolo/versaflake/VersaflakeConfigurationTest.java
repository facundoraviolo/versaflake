package ar.com.facundoraviolo.versaflake;

import ar.com.facundoraviolo.versaflake.exceptions.InvalidBitConfigurationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VersaflakeConfigurationTest {

    @Test
    public void testDefaultConfiguration() {
        VersaflakeConfiguration config = new VersaflakeConfiguration.Builder().build();
        assertEquals(1704067200000L, config.getStartEpoch());
        assertEquals(10, config.getNodeIdBits());
        assertEquals(12, config.getSequenceBits());
    }

    @Test
    public void testCustomConfiguration() {
        VersaflakeConfiguration config = new VersaflakeConfiguration.Builder()
                .startEpoch(1609459200000L)
                .nodeIdBits(12)
                .sequenceBits(10)
                .build();
        assertEquals(1609459200000L, config.getStartEpoch());
        assertEquals(12, config.getNodeIdBits());
        assertEquals(10, config.getSequenceBits());
    }

    @Test
    public void testInvalidBitConfiguration() {
        assertThrows(InvalidBitConfigurationException.class, () -> new VersaflakeConfiguration.Builder()
                .nodeIdBits(15)
                .sequenceBits(10)
                .build());
    }

}
