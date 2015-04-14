package MarketEntities;

import Model.IssueStockRequest;
import Service.ConnectionError;
import Service.IBroker;

import java.util.List;

/**
 * Created by Felix on 14.04.2015.
 */
public abstract class ISRContainer {

    public abstract void addIssueStocksRequest(IssueStockRequest isr, String transactionId) throws ConnectionError;

    public abstract List<IssueStockRequest> takeIssueStockRequests(String transactionId) throws ConnectionError;

    public abstract void subscribe(IBroker broker, String transactionId) throws ConnectionError;

}
