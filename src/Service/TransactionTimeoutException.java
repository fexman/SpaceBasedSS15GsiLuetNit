package Service;

/**
 * Created by Felix on 06.05.2015.
 */
public class TransactionTimeoutException extends Exception {

    private Exception e;

    public TransactionTimeoutException(String message) {
        e = new Exception(message);
    }

    public TransactionTimeoutException(Exception e) {
        this.e = e;
    }

    public Exception getException() {
        return e;
    }

    public void printStackTrace() {
        e.printStackTrace();
    }

    public String getMessage() {
        return e.getMessage();
    }
}