package com.facundoraviolo.versaflake;

import com.facundoraviolo.versaflake.exceptions.ClockMovedBackwardException;
import com.facundoraviolo.versaflake.exceptions.InvalidNodeIdException;

import static java.lang.System.currentTimeMillis;

/**
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

    public static class Builder {

        private final long nodeId;
        private VersaflakeConfiguration configuration;

        public Builder(long nodeId) {
            this.nodeId = nodeId;
        }

        public Builder configuration(VersaflakeConfiguration configuration) {
            this.configuration = configuration;
            return this;
        }

        public VersaflakeIDGenerator build() {
            if (configuration == null) {
                configuration = new VersaflakeConfiguration.Builder().build();
            }
            return new VersaflakeIDGenerator(nodeId, configuration);
        }

    }

}
