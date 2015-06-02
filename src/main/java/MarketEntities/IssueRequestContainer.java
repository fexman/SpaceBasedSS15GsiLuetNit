package MarketEntities;

import MarketEntities.XVSM.ISubscribeable;
import Model.IssueRequest;
import Model.IssueStockRequest;
import Service.ConnectionErrorException;
import Service.TransactionTimeoutException;

import java.util.List;

/**
 * Created by Felix on 14.04.2015.
 */
public abstract class IssueRequestContainer implements ISubscribeable {

    public abstract void addIssueRequest(IssueRequest ir, String transactionId) throws ConnectionErrorException;

    public abstract List<IssueRequest> takeIssueRequests(String transactionId) throws ConnectionErrorException, TransactionTimeoutException;

}
