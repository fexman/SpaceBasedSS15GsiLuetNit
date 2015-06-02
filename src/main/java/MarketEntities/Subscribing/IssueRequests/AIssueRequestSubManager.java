package MarketEntities.Subscribing.IssueRequests;

import MarketEntities.Subscribing.ASubManager;

/**
 * Created by Felix on 16.04.2015.
 */
public abstract class AIssueRequestSubManager extends ASubManager<IIssueRequestSub> {

    public AIssueRequestSubManager(IIssueRequestSub subscription) {
        super(subscription);
    }
}
