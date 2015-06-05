package Util;

/**
 * Created by Felix on 23.04.2015.
 */
public enum Container {
    ISSUED_REQUESTS("issuedRequests"),
    TRANSACTION_HISTORY("transactionHistory"),
    STOCK_PRICES("stockPrices"),
    TRADE_ORDERS("tradeOrders"),
    BROKER_SPSUPPORT("brokerStockPrices"),
    BROKER_TOSUPPORT("brokerTradeOrders"),
    DEPOT_COMPANY_TOKEN("depot_company_"),
    DEPOT_INVESTOR_TOKEN("depot_investor_"),
    FONDS_INDEX_CONTAINER("fondsIndexContainer");

    private final String text;

    /**
     * @param text
     */
    Container(final String text) {
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
