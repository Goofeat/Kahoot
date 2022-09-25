package kahoot;

public class InvalidQuizFormatException extends Exception {
    public InvalidQuizFormatException() {
        super("No line found");
    }
}