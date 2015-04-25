package Service;

/**
 * Created by j0h1 on 24.04.2015.
 */
public class InvalidTradeOrderException extends Exception {

    public InvalidTradeOrderException() {
        super();
    }

    public InvalidTradeOrderException(String message) {
        super(message);
    }

}
