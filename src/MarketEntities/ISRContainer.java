package MarketEntities;

import MarketEntities.XVSM.ISubscribeable;
import Model.IssueStockRequest;
import Service.ConnectionErrorException;
import Service.TransactionTimeoutException;

import java.util.List;

/**
 * Created by Felix on 14.04.2015.
 */
public abstract class ISRContainer implements ISubscribeable {

    public abstract void addIssueStocksRequest(IssueStockRequest isr, String transactionId) throws ConnectionErrorException;

    public abstract List<IssueStockRequest> takeIssueStockRequests(String transactionId) throws ConnectionErrorException, TransactionTimeoutException;

}
