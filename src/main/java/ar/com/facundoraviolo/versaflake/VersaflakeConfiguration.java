package ar.com.facundoraviolo.versaflake;

import ar.com.facundoraviolo.versaflake.exceptions.InvalidBitConfigurationException;

/**
 * <b>Versaflake Configuration</b>
 * <p>
 * This class encapsulates the configuration required for the <b>Versaflake ID</b> generator.
 * It allows specifying the number of bits for each component (timestamp, node ID, sequence)
 * as well as the epoch (start date) and strict mode behavior.
 * <p>
 * The default values are:
 * <ul>
 *     <li>Start epoch: 2025-01-01 00:00:00 UTC (1735689600000L)</li>
 *     <li>Timestamp Bits: 41 (supports dates until 2159)</li>
 *     <li>Node ID Bits: 10 (provides 1024 possible node IDs)</li>
 *     <li>Sequence Bits: 12 (provides 4096 possible sequence values)</li>
 *     <li>Strict Mode: false (wait for clock to catch up instead of throwing exception)</li>
 * </ul>
 * This configuration can be customized using the Builder pattern.
 *
 * @author Facundo Raviolo
 */
public class VersaflakeConfiguration {

    private static final long DEFAULT_START_EPOCH = 1735689600000L;
    private static final long DEFAULT_TIMESTAMP_BITS = 41L;
    private static final long DEFAULT_NODE_ID_BITS = 10L;
    private static final long DEFAULT_SEQUENCE_BITS = 12L;
    private static final boolean DEFAULT_STRICT_MODE = false;

    private final long startEpoch;
    private final long timestampBits;
    private final long nodeIdBits;
    private final long sequenceBits;
    private final boolean strictMode;

    VersaflakeConfiguration(long startEpoch, long timestampBits, long nodeIdBits,
                            long sequenceBits, boolean strictMode) {
        this.startEpoch = startEpoch;
        this.timestampBits = timestampBits;
        this.nodeIdBits = nodeIdBits;
        this.sequenceBits = sequenceBits;
        this.strictMode = strictMode;
    }

    /**
     * Factory method to create a VersaflakeConfigurationBuilder instance.
     * @return A new VersaflakeConfigurationBuilder instance.
     */
    public static VersaflakeConfigurationBuilder builder() {
        return new VersaflakeConfigurationBuilder();
    }

    protected long getStartEpoch() {
        return startEpoch;
    }

    protected long getTimestampBits() {
        return timestampBits;
    }

    protected long getNodeIdBits() {
        return nodeIdBits;
    }

    protected long getSequenceBits() {
        return sequenceBits;
    }

    protected boolean isStrictMode() {
        return strictMode;
    }

    /**
     * Builder for configuring the Versaflake ID generator.
     * <p>
     * Allows setting all components of the ID generation:
     * <ul>
     *     <li>Timestamp bits: controls how far into the future IDs can be generated</li>
     *     <li>Node ID bits: controls how many different nodes can generate IDs</li>
     *     <li>Sequence bits: controls how many IDs can be generated per millisecond per node</li>
     *     <li>Start epoch: the reference timestamp for ID generation</li>
     *     <li>Strict mode: whether to throw an exception on clock movement backward</li>
     * </ul>
     * The total number of bits (timestamp + nodeId + sequence) must not exceed 63.
     */
    public static class VersaflakeConfigurationBuilder {
        private long startEpoch;
        private long timestampBits;
        private long nodeIdBits;
        private long sequenceBits;
        private boolean strictMode;

        VersaflakeConfigurationBuilder() {
            this.startEpoch = DEFAULT_START_EPOCH;
            this.timestampBits = DEFAULT_TIMESTAMP_BITS;
            this.nodeIdBits = DEFAULT_NODE_ID_BITS;
            this.sequenceBits = DEFAULT_SEQUENCE_BITS;
            this.strictMode = DEFAULT_STRICT_MODE;
        }

        /**
         * Configures the epoch (start date) for ID generation.
         * If not set, the default value (January 1, 2024, 00:00:00 UTC) will be used.
         * @param startEpoch The timestamp in milliseconds representing the start date.
         * @return The Builder for chaining configurations.
         */
        public VersaflakeConfigurationBuilder startEpoch(long startEpoch) {
            this.startEpoch = startEpoch;
            return this;
        }

        /**
         * Configures the number of bits for the timestamp.
         * If not set, the default value (41 bits) will be used.
         * @param timestampBits The number of bits for the timestamp.
         * @return The Builder for chaining configurations.
         */
        public VersaflakeConfigurationBuilder timestampBits(long timestampBits) {
            this.timestampBits = timestampBits;
            return this;
        }

        /**
         * Configures the number of bits for the node ID.
         * If not set, the default value (10 bits) will be used.
         * @param nodeIdBits The number of bits for the node ID.
         * @return The Builder for chaining configurations.
         */
        public VersaflakeConfigurationBuilder nodeIdBits(long nodeIdBits) {
            this.nodeIdBits = nodeIdBits;
            return this;
        }

        /**
         * Configures the number of bits for the sequence.
         * If not set, the default value (12 bits) will be used.
         * @param sequenceBits The number of bits for the sequence.
         * @return The Builder for chaining configurations.
         */
        public VersaflakeConfigurationBuilder sequenceBits(long sequenceBits) {
            this.sequenceBits = sequenceBits;
            return this;
        }

        /**
         * Enables strict mode, which will throw a ClockMovedBackwardException
         * when a backwards movement of the system clock is detected.
         * If not enabled (default), the generator will wait for the clock to catch up.
         * @return The Builder for chaining configurations.
         */
        public VersaflakeConfigurationBuilder strictMode() {
            this.strictMode = true;
            return this;
        }

        /**
         * Builds the VersaflakeConfiguration instance with the configured values.
         * @return The generated VersaflakeConfiguration.
         * @throws InvalidBitConfigurationException if the total bits exceeds 63 or any component has 0 or negative bits
         */
        public VersaflakeConfiguration build() {
            long totalBits = timestampBits + nodeIdBits + sequenceBits;
            if (totalBits > 63) {
                throw new InvalidBitConfigurationException(
                        String.format("Total bits (timestamp: %d + nodeId: %d + sequence: %d = %d) cannot exceed 63",
                                timestampBits, nodeIdBits, sequenceBits, totalBits));
            }
            if (timestampBits <= 0 || nodeIdBits <= 0 || sequenceBits <= 0) {
                throw new InvalidBitConfigurationException(
                        "Timestamp bits, node ID bits and sequence bits must all be greater than 0");
            }
            return new VersaflakeConfiguration(this.startEpoch, this.timestampBits,
                    this.nodeIdBits, this.sequenceBits, this.strictMode);
        }

    }

}
