package exception;

/*
 * Custom exception used for handling validation errors, 
 * such as incorrect user input or invalid data.
 */
public class ValidationException extends Exception {

    /*
     * Constructor that creates a ValidationException with a custom error message.
     * @param message A descriptive message explaining the validation error.
     */
    public ValidationException(String message) {
        super(message);
    }

    /*
     * Constructor that creates a ValidationException with both a message and a cause.
     * @param message A descriptive message explaining the validation error.
     * @param cause The original exception that triggered this validation error.
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}