package backend.error;

public class SyntaxError extends RuntimeException {
    public SyntaxError(String s) {
        super(s);
    }
}
