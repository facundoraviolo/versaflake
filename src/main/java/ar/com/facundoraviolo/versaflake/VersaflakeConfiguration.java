package ar.com.facundoraviolo.versaflake;

import ar.com.facundoraviolo.versaflake.exceptions.InvalidBitConfigurationException;

/**
 * <b>Versaflake Configuration</b>
 * <p>
 * This class encapsulates the configuration required for the <b>Versaflake ID</b> generator.
 * It allows specifying the number of bits for the node ID and sequence, as well as the epoch (start date).
 * <p>
 * The default values are:
 * <ul>
 *     <li>Start epoch: 2024-01-01 00:00:00 UTC (1704067200000L)</li>
 *     <li>Node ID Bits: 10 (provides 1024 possible node IDs)</li>
 *     <li>Sequence Bits: 12 (provides 4096 possible sequence values)</li>
 * </ul>
 * This configuration can be customized using the Builder pattern.
 *
 * @author Facundo Raviolo
 */
public class VersaflakeConfiguration {

    private static final long DEFAULT_START_EPOCH = 1704067200000L;
    private static final long DEFAULT_NODE_ID_BITS = 10L;
    private static final long DEFAULT_SEQUENCE_BITS = 12L;

    private final long startEpoch;
    private final long nodeIdBits;
    private final long sequenceBits;

    VersaflakeConfiguration(long startEpoch, long nodeIdBits, long sequenceBits) {
        this.startEpoch = startEpoch;
        this.nodeIdBits = nodeIdBits;
        this.sequenceBits = sequenceBits;
    }

    /**
     * Factory method to create a VersaflakeConfigurationBuilder instance.
     * @return A new VersaflakeConfigurationBuilder instance.
     */
    public static VersaflakeConfigurationBuilder builder() {
        return new VersaflakeConfigurationBuilder();
    }

    /**
     * Gets the value of the start epoch.
     * @return The start epoch value in milliseconds.
     */
    protected long getStartEpoch() {
        return startEpoch;
    }

    /**
     * Gets the number of bits configured for the node ID.
     * @return The number of bits for the node ID.
     */
    protected long getNodeIdBits() {
        return nodeIdBits;
    }

    /**
     * Gets the number of bits configured for the sequence.
     * @return The number of bits for the sequence.
     */
    protected long getSequenceBits() {
        return sequenceBits;
    }

    /**
     * Builder for configuring the Versaflake ID generator.
     * <p>
     * Allows setting the start epoch, the number of bits for the node ID, and the number of bits for the sequence.
     */
    public static class VersaflakeConfigurationBuilder {

        private long startEpoch;
        private long nodeIdBits;
        private long sequenceBits;

        VersaflakeConfigurationBuilder() {
            this.startEpoch = DEFAULT_START_EPOCH;
            this.nodeIdBits = DEFAULT_NODE_ID_BITS;
            this.sequenceBits = DEFAULT_SEQUENCE_BITS;
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
         * Builds the VersaflakeConfiguration instance with the configured values.
         * @return The generated VersaflakeConfiguration.
         * @throws InvalidBitConfigurationException if the sum of nodeIdBits and sequenceBits is not equal to 22.
         */
        public VersaflakeConfiguration build() {
            if (nodeIdBits + sequenceBits != 22) {
                throw new InvalidBitConfigurationException();
            }
            return new VersaflakeConfiguration(this.startEpoch, this.nodeIdBits, this.sequenceBits);
        }

    }

}
