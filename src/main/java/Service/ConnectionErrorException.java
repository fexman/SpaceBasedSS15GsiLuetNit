package Service;

/**
 * Created by Felix on 06.04.2015.
 */
public class ConnectionErrorException extends Exception {

    private Exception e;

    public ConnectionErrorException(String message) {
        e = new Exception(message);
    }

    public ConnectionErrorException(Exception e) {
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
