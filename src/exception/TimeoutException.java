package exception;

/*
 * Custom exception used to signal that an operation took too long to complete.
 * Often used for timeout-related scenarios.
 */
public class TimeoutException extends Exception {

    /*
     * Constructor that creates a TimeoutException with a custom error message.
     * @param message A descriptive message explaining the timeout issue.
     */
    public TimeoutException(String message) {
        super(message);
    }

    /*
     * Constructor that creates a TimeoutException with both a message and a cause.
     * @param message A descriptive message explaining the timeout issue.
     * @param cause The original exception that triggered this timeout error.
     */
    public TimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}