package com.facundoraviolo.versaflake;

import com.facundoraviolo.versaflake.exceptions.ClockMovedBackwardException;
import com.facundoraviolo.versaflake.exceptions.InvalidNodeIdException;

import static java.lang.System.currentTimeMillis;

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
public class VersaflakeIDGenerator {

    private final long nodeIdShift;
    private final long timestampShift;
    private final long sequenceMask;
    private final long epoch;
    private final long nodeId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    private VersaflakeIDGenerator(long nodeId, VersaflakeConfiguration configuration) {
        long nodeIdBits = configuration.getNodeIdBits();
        long sequenceBits = configuration.getSequenceBits();
        long maxNodeId = ~(-1L << nodeIdBits);
        this.nodeIdShift = sequenceBits;
        this.timestampShift = nodeIdBits + sequenceBits;
        this.sequenceMask = ~(-1L << sequenceBits);
        this.epoch = configuration.getStartEpoch();
        this.nodeId = nodeId;
        if (nodeId < 0 || nodeId > maxNodeId) {
            throw new InvalidNodeIdException(maxNodeId);
        }
    }

    /**
     * Generates a unique <b>Versaflake ID</b>.
     * <p>
     * This method is thread-safe and ensures there are no collisions
     * even when generating multiple IDs within the same millisecond.
     *
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

    /**
     * Builder class for creating instances of the Versaflake ID generator with customizable configurations.
     * <p>
     * The builder allows users to set the node ID and an optional custom configuration for the generator.
     * If a configuration is not explicitly provided, the default configuration is used.
     * </p>
     * <p>
     * The builder follows the Builder design pattern, providing a fluent interface for easy chaining of method calls.
     * </p>
     */
    public static class Builder {

        private final long nodeId;
        private VersaflakeConfiguration configuration;

        /**
         * Constructor to initialize the Builder with the nodeId.
         * <p>
         * The allowed values for nodeId depend on the number of bits available for the node ID.
         * For example, if nodeIdBits is 10, the valid nodeId range is from 0 to 1023.
         * </p>
         *
         * @param nodeId The unique identifier for the node.
         */
        public Builder(long nodeId) {
            this.nodeId = nodeId;
        }

        /**
         * Sets the custom configuration for the Versaflake ID generator.
         * <p>
         * If no configuration is provided, the default configuration will be used when the builder builds the generator.
         * </p>
         *
         * @param configuration The custom configuration to use for the generator.
         * @return The Builder for chaining configurations.
         */
        public Builder configuration(VersaflakeConfiguration configuration) {
            this.configuration = configuration;
            return this;
        }

        /**
         * Builds the VersaflakeIDGenerator instance with the provided node ID and configuration.
         * <p>
         * If no configuration was provided via the {@link #configuration(VersaflakeConfiguration)} method,
         * a default configuration will be used when building the generator.
         * </p>
         *
         * @return The configured VersaflakeIDGenerator instance.
         */
        public VersaflakeIDGenerator build() {
            if (configuration == null) {
                configuration = new VersaflakeConfiguration.Builder().build();
            }
            return new VersaflakeIDGenerator(nodeId, configuration);
        }

    }

}
