package com.facundoraviolo.versaflake;

import com.facundoraviolo.versaflake.exceptions.InvalidBitConfigurationException;

/**
 * @author Facundo Raviolo
 */
public class VersaflakeConfiguration {

    private static final long DEFAULT_START_EPOCH = 1704067200000L;
    private static final long DEFAULT_NODE_ID_BITS = 10L;
    private static final long DEFAULT_SEQUENCE_BITS = 12L;

    private final long startEpoch;
    private final long nodeIdBits;
    private final long sequenceBits;

    private VersaflakeConfiguration(Builder builder) {
        this.startEpoch = builder.startEpoch;
        this.nodeIdBits = builder.nodeIdBits;
        this.sequenceBits = builder.sequenceBits;
    }

    protected long getStartEpoch() {
        return startEpoch;
    }

    protected long getNodeIdBits() {
        return nodeIdBits;
    }

    protected long getSequenceBits() {
        return sequenceBits;
    }

    public static class Builder {

        private long startEpoch;
        private long nodeIdBits;
        private long sequenceBits;

        public Builder() {
            this.startEpoch = DEFAULT_START_EPOCH;
            this.nodeIdBits = DEFAULT_NODE_ID_BITS;
            this.sequenceBits = DEFAULT_SEQUENCE_BITS;
        }

        public Builder startEpoch(long startEpoch) {
            this.startEpoch = startEpoch;
            return this;
        }

        public Builder nodeIdBits(long nodeIdBits) {
            this.nodeIdBits = nodeIdBits;
            return this;
        }

        public Builder sequenceBits(long sequenceBits) {
            this.sequenceBits = sequenceBits;
            return this;
        }

        public VersaflakeConfiguration build() {
            if (nodeIdBits + sequenceBits != 22) {
                throw new InvalidBitConfigurationException();
            }
            return new VersaflakeConfiguration(this);
        }

    }

}
