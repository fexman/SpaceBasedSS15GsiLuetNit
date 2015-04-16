package MarketEntities;

import Service.Subscribing.IssueStockRequests.AIssueStockRequestSubManager;
import Model.IssueStockRequest;
import Service.ConnectionError;

import java.util.List;

/**
 * Created by Felix on 14.04.2015.
 */
public abstract class IssueStockRequestContainer {

    public abstract void addIssueStocksRequest(IssueStockRequest isr, String transactionId) throws ConnectionError;

    public abstract List<IssueStockRequest> takeIssueStockRequests(String transactionId) throws ConnectionError;

    public abstract void subscribe(AIssueStockRequestSubManager subscriber, String transactionId) throws ConnectionError;

}
