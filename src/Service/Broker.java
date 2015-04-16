package Service;

import Factory.IFactory;
import MarketEntities.ISRContainer;
import MarketEntities.TradeOrdersContainer;
import Model.IssueStockRequest;
import Model.TradeOrder;

import java.util.List;

/**
 * Created by Felix on 11.04.2015.
 */
public class Broker extends Service {

    private ISRContainer isrContainer;
    private TradeOrdersContainer tradeOrdersContainer;

    public Broker(IFactory factory) {
        super(factory);
        isrContainer = factory.newISRContainer();
        tradeOrdersContainer = factory.newTradeOrdersContainer();
    }

    public void startBroking() throws ConnectionError {
            takeAndProcessISRs();
            isrContainer.subscribe(factory.newSubscriber(this),null);
    }

    public void takeAndProcessISRs() throws ConnectionError {
        String transactionId = null;
        try {

            transactionId = factory.createTransaction();
            List<IssueStockRequest> isrs = isrContainer.takeIssueStockRequests(transactionId);
            if (isrs.size() > 0) {
                System.out.println("Got "+isrs.size()+" new ISRs!");
                for (IssueStockRequest isr : isrs) {

                    //TODO: GET MARKET VALUE INSTEAD OF ISR PRICE DIRECTLY
                    TradeOrder order = new TradeOrder(isr.getCompany(),isr.getCompany(),isr.getAmount(),isr.getPrice());
                    tradeOrdersContainer.addOrUpdateOrder(order, transactionId);
                }

            }

            factory.commitTransaction(transactionId);
        } catch (ConnectionError e) {
            try {
                factory.rollbackTransaction(transactionId);
                throw new ConnectionError(e);
            } catch (ConnectionError ex) {
                throw new ConnectionError(ex);
            }
        }

    }
}
