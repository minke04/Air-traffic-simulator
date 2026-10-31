package exception;

/*  
 * Custom exception used for handling errors during file processing operations, 
 * such as reading or writing CSV files.
 */
public class FileProcessingException extends Exception {
    
    /*  
     * Constructor that creates a FileProcessingException with a custom error message.
     * @param message A descriptive message explaining the cause of the error.
     */
    public FileProcessingException(String message) {
        super(message);
    }

    /*  
     * Constructor that creates a FileProcessingException with both a message and a cause.
     * @param message A descriptive message explaining the error.
     * @param cause The original exception that triggered this error.
     */
    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}