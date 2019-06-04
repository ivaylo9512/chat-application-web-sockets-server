package exceptions;

public class UsernameExistsException extends RuntimeException {
    public UsernameExistsException(String exception) {
        super(exception);
    }
}
