package MarketEntities.Subscribing.IssueStockRequests;

import MarketEntities.Subscribing.ASubManager;

/**
 * Created by Felix on 16.04.2015.
 */
public abstract class AISRSubManager extends ASubManager<IISRRequestSub> {

    public AISRSubManager(IISRRequestSub subscription) {
        super(subscription);
    }
}
