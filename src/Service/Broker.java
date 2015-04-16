package Service;

import Factory.IFactory;
import MarketEntities.ISRContainer;
import Model.IssueStockRequest;

import java.util.List;

/**
 * Created by Felix on 11.04.2015.
 */
public class Broker extends Service {

    private ISRContainer isrContainer;

    public Broker(IFactory factory) {
        super(factory);
        isrContainer = factory.newISRContainer();
    }

    public void startBroking() throws ConnectionError {

            takeISRs();
            isrContainer.subscribe(factory.newSubscriber(this),null);

    }

    public void takeISRs() throws ConnectionError {
        String transactionId = null;
        try {

            transactionId = factory.createTransaction();
            List<IssueStockRequest> isrs = isrContainer.takeIssueStockRequests(transactionId);

            for (IssueStockRequest isr : isrs) {

                //TODO add order to TRADEABLE_ORDERS container

                System.out.println(isr.toString());
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
