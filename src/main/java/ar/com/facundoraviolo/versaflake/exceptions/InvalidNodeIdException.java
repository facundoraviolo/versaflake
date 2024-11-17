package ar.com.facundoraviolo.versaflake.exceptions;

/**
 * Exception thrown when the provided node ID is invalid.
 * <p>
 * This exception is triggered when the node ID is outside the allowed range
 * based on the number of bits allocated to the node ID. The valid range is
 * determined as {@code 0 <= nodeId <= maxNodeId}, where {@code maxNodeId} is
 * calculated as {@code (2^nodeIdBits) - 1}.
 * </p>
 *
 * <p>
 * Example:
 * If {@code nodeIdBits} is 10, the valid range for {@code nodeId} is from 0 to 1023.
 * A node ID of 1024 or higher will trigger this exception.
 * </p>
 *
 * @author Facundo Raviolo
 */
public class InvalidNodeIdException extends RuntimeException {

    public InvalidNodeIdException(long maxNodeId) {
        super("nodeId must be between 0 and " + maxNodeId);
    }

}