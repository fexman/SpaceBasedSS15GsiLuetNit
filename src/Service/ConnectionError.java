package Service;

/**
 * Created by Felix on 06.04.2015.
 */
public class ConnectionError extends Exception {

    private Exception e;

    public ConnectionError(Exception e) {
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
