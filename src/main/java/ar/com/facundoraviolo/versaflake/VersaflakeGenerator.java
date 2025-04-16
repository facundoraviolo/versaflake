package ar.com.facundoraviolo.versaflake;

import ar.com.facundoraviolo.versaflake.exceptions.ClockMovedBackwardException;
import ar.com.facundoraviolo.versaflake.exceptions.InvalidNodeIdException;

import java.time.Clock;

/**
 * <b>Versaflake ID Generator</b>
 * <p>
 * This generator creates unique IDs efficiently, using the current time in milliseconds,
 * a worker node identifier, and a sequential counter to avoid collisions.
 * <p>
 * If no custom configuration is provided when constructing the generator,
 * the default configuration will be used. The default configuration includes:
 * <ul>
 *     <li>Start epoch: 2024-01-01 00:00:00 UTC (1704067200000L)</li>
 *     <li>Node ID Bits: 10 (provides 1024 possible node IDs)</li>
 *     <li>Sequence Bits: 12 (provides 4096 possible sequence values)</li>
 * </ul>
 *
 * @author Facundo Raviolo
 */
public class VersaflakeGenerator {

    private final Clock clock;
    private final long nodeIdShift;
    private final long timestampShift;
    private final long sequenceMask;
    private final long epoch;
    private final long nodeId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    VersaflakeGenerator(Clock clock, long nodeId, long startEpoch, long nodeIdBits, long sequenceBits) {
        this.clock = clock;
        long maxNodeId = ~(-1L << nodeIdBits);
        this.nodeIdShift = sequenceBits;
        this.timestampShift = nodeIdBits + sequenceBits;
        this.sequenceMask = ~(-1L << sequenceBits);
        this.epoch = startEpoch;
        this.nodeId = nodeId;
        if (nodeId < 0 || nodeId > maxNodeId) {
            throw new InvalidNodeIdException(maxNodeId);
        }
    }

    /**
     * Factory method to create a VersaflakeGeneratorBuilder instance.
     * <p>
     * The allowed values for nodeId depend on the number of bits available for the node ID.
     * For example, if nodeIdBits is 10, the valid nodeId range is from 0 to 1023.
     * @param nodeId The unique identifier for the node.
     * @return A new VersaflakeGeneratorBuilder instance.
     */
    public static VersaflakeGeneratorBuilder builder(long nodeId) {
        return new VersaflakeGeneratorBuilder(nodeId);
    }

    /**
     * Generates a unique <b>Versaflake ID</b>.
     * <p>
     * This method is thread-safe and ensures there are no collisions
     * even when generating multiple IDs within the same millisecond.
     * @return The generated ID.
     */
    public synchronized long nextId() {
        long timestamp = currentTimeMillis();
        if (timestamp < lastTimestamp) {
            throw new ClockMovedBackwardException(lastTimestamp, timestamp);
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = waitForNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        return ((timestamp - epoch) << timestampShift)
                | (nodeId << nodeIdShift)
                | sequence;
    }

    private long waitForNextMillis(long lastTimestamp) {
        long timestamp = currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTimeMillis();
        }
        return timestamp;
    }

    private long currentTimeMillis() {
        return clock.millis();
    }

    /**
     * Builder class for creating instances of the Versaflake ID generator with customizable configurations.
     * <p>
     * The builder allows users to set the node ID and an optional custom configuration for the generator.
     * If a configuration is not explicitly provided, the default configuration is used.
     * <p>
     * The builder follows the Builder design pattern, providing a fluent interface for easy chaining of method calls.
     */
    public static class VersaflakeGeneratorBuilder {

        private final long nodeId;
        private VersaflakeConfiguration configuration;

        VersaflakeGeneratorBuilder(long nodeId) {
            this.nodeId = nodeId;
        }

        /**
         * Sets the custom configuration for the Versaflake ID generator.
         * <p>
         * If no configuration is provided, the default configuration will be used when the builder builds the generator.
         * @param configuration The custom configuration to use for the generator.
         * @return The Builder for chaining configurations.
         */
        public VersaflakeGeneratorBuilder configuration(VersaflakeConfiguration configuration) {
            this.configuration = configuration;
            return this;
        }

        /**
         * Builds the VersaflakeGenerator instance with the provided node ID and configuration.
         * <p>
         * If no configuration was provided via the {@link #configuration(VersaflakeConfiguration)} method,
         * a default configuration will be used when building the generator.
         * @return The configured VersaflakeGenerator instance.
         */
        public VersaflakeGenerator build() {
            if (configuration == null) {
                configuration = new VersaflakeConfiguration.VersaflakeConfigurationBuilder().build();
            }
            return new VersaflakeGenerator(Clock.systemUTC(), nodeId, configuration.getStartEpoch(),
                    configuration.getNodeIdBits(), configuration.getSequenceBits());
        }

    }

}
