package kahoot.exception;

public class InvalidQuizFormatException extends Exception {
    public InvalidQuizFormatException() {
        super("No line found");
    }
}