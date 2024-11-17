package ar.com.facundoraviolo.versaflake.exceptions;

/**
 * Exception thrown when the system clock moves backwards.
 * <p>
 * This exception is triggered if the current timestamp is earlier than the last timestamp
 * recorded by the generator, indicating that the system clock has been adjusted backwards.
 * </p>
 *
 * <p>
 * This scenario can occur due to manual clock adjustments, synchronization issues with the system clock,
 * or other environmental factors. Since the ID generation depends on a strictly increasing timestamp,
 * moving the clock backward compromises the integrity of the generated IDs.
 * </p>
 *
 * <p>
 * Example:
 * Suppose the generator last produced an ID with a timestamp of {@code 1690000000000L}.
 * If the system clock then reports a timestamp of {@code 1689999999999L}, this exception will be thrown.
 * </p>
 *
 * <p>
 * Mitigation:
 * If you encounter this exception frequently, consider:
 * <ul>
 *     <li>Ensuring your system clock is synchronized with a reliable time source (e.g., NTP).</li>
 *     <li>Using a monotonic clock source if available on your platform.</li>
 *     <li>Investigating any manual or automated adjustments to the system time.</li>
 * </ul>
 * </p>
 *
 * @author Facundo Raviolo
 */
public class ClockMovedBackwardException extends RuntimeException {

    public ClockMovedBackwardException(long lastTimestamp, long currentTimestamp) {
        super(String.format("System clock moved backward. Last timestamp: %d, current timestamp: %d", lastTimestamp, currentTimestamp));
    }

}
