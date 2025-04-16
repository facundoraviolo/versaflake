package ar.com.facundoraviolo.versaflake;

import ar.com.facundoraviolo.versaflake.exceptions.InvalidBitConfigurationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VersaflakeConfigurationTest {

    @Test
    public void testDefaultConfiguration() {
        VersaflakeConfiguration config = VersaflakeConfiguration.builder().build();
        assertEquals(1735689600000L, config.getStartEpoch());
        assertEquals(41, config.getTimestampBits());
        assertEquals(10, config.getNodeIdBits());
        assertEquals(12, config.getSequenceBits());
        assertFalse(config.isStrictMode());
    }

    @Test
    public void testCustomConfiguration() {
        VersaflakeConfiguration config = VersaflakeConfiguration.builder()
                .startEpoch(1609459200000L)
                .timestampBits(41)
                .nodeIdBits(12)
                .sequenceBits(10)
                .strictMode()
                .build();
        assertEquals(1609459200000L, config.getStartEpoch());
        assertEquals(12, config.getNodeIdBits());
        assertEquals(10, config.getSequenceBits());
    }

    @Test
    public void testInvalidBitConfiguration() {
        assertThrows(InvalidBitConfigurationException.class, () -> VersaflakeConfiguration.builder()
                .nodeIdBits(13)
                .sequenceBits(10)
                .build());
    }

}
