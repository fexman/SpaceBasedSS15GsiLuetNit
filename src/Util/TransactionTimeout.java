package Util;

/**
 * Created by Felix on 01.05.2015.
 */
public enum TransactionTimeout {
    TRY_ONCE("tryOnce"),
    INFINITE("infinite"),
    DEFAULT("default");

    private final String text;

    /**
     * @param text
     */
    TransactionTimeout(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
}
