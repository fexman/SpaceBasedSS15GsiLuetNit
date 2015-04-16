package Service.Subscribing.IssueStockRequests;

import Service.Broker;

/**
 * Created by Felix on 16.04.2015.
 */
public abstract class AIssueStockRequestSubManager {

    protected IIssueStockRequestSub subscription;

    public AIssueStockRequestSubManager(IIssueStockRequestSub subscription) {
        this.subscription = subscription;
    }
}
