package ar.com.facundoraviolo.versaflake;

import ar.com.facundoraviolo.versaflake.exceptions.ClockMovedBackwardException;
import ar.com.facundoraviolo.versaflake.exceptions.InvalidNodeIdException;

/**
 * <b>Versaflake ID Generator</b>
 * <p>
 * This generator creates unique IDs efficiently, using the current time in milliseconds,
 * a worker node identifier, and a sequential counter to avoid collisions.
 * <p>
 * If no custom configuration is provided when constructing the generator,
 * the default configuration will be used. The default configuration includes:
 * <ul>
 *     <li>Start epoch: 2025-01-01 00:00:00 UTC (1735689600000L)</li>
 *     <li>Timestamp Bits: 41 (supports dates until 2159)</li>
 *     <li>Node ID Bits: 10 (provides 1024 possible node IDs)</li>
 *     <li>Sequence Bits: 12 (provides 4096 possible sequence values)</li>
 *     <li>Strict Mode: false (wait for clock to catch up instead of throwing exception)</li>
 * </ul>
 * <p>
 * The generator supports flexible bit allocation for all components,
 * allowing custom configurations as long as the total bits do not exceed 63.
 * Users should carefully consider their specific requirements when customizing bit allocation:
 * <ul>
 *     <li>Fewer timestamp bits = shorter time range before overflow</li>
 *     <li>Fewer node ID bits = fewer possible nodes</li>
 *     <li>Fewer sequence bits = higher collision probability within the same millisecond</li>
 * </ul>
 *
 * @author Facundo Raviolo
 */
public class VersaflakeGenerator {

    private final long nodeIdShift;
    private final long timestampShift;
    private final long sequenceMask;
    private final long timestampMask;
    private final long epoch;
    private final long nodeId;
    private final boolean strictMode;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    VersaflakeGenerator(long nodeId, long startEpoch, long nodeIdBits,
                        long sequenceBits, long timestampBits, boolean strictMode) {
        long maxNodeId = ~(-1L << nodeIdBits);
        this.nodeIdShift = sequenceBits;
        this.timestampShift = nodeIdBits + sequenceBits;
        this.sequenceMask = ~(-1L << sequenceBits);
        this.timestampMask = ~(-1L << timestampBits);
        this.epoch = startEpoch;
        this.nodeId = nodeId;
        this.strictMode = strictMode;

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
     * @throws ClockMovedBackwardException if strict mode is enabled and the system clock moves backward
     */
    public synchronized long nextId() {
        long timestamp = currentTimeMillis();
        if (timestamp < lastTimestamp) {
            if (strictMode) {
                throw new ClockMovedBackwardException(lastTimestamp, timestamp);
            }
            timestamp = waitForNextMillis(lastTimestamp);
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

        long timeDiff = (timestamp - epoch) & timestampMask;
        return (timeDiff << timestampShift)
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
        return System.currentTimeMillis();
    }

    /**
     * Builder class for creating instances of the Versaflake ID generator with customizable configurations.
     * <p>
     * The builder allows users to set the node ID and an optional custom configuration for the generator.
     * If a configuration is not explicitly provided, the default configuration is used.
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
            return new VersaflakeGenerator(
                    nodeId,
                    configuration.getStartEpoch(),
                    configuration.getNodeIdBits(),
                    configuration.getSequenceBits(),
                    configuration.getTimestampBits(),
                    configuration.isStrictMode()
            );
        }

    }

}
