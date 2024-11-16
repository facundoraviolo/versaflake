package com.facundoraviolo.versaflake.exceptions;

/**
 * Exception thrown when the bit configuration for the ID generator is invalid.
 * <p>
 * This exception is triggered when the sum of the bits assigned to the node ID
 * and the sequence does not match the expected total (22 bits in this case).
 * </p>
 *
 * <p>
 * Example:
 * If {@code nodeIdBits} is 8 and {@code sequenceBits} is 14, their sum equals 22,
 * and the configuration is valid. If the sum deviates from 22, this exception will be thrown.
 * </p>
 *
 * @author Facundo Raviolo
 */
public class InvalidBitConfigurationException extends RuntimeException {

    public InvalidBitConfigurationException() {
        super("The sum of nodeIdBits and sequenceBits must be equal to 22.");
    }

}
