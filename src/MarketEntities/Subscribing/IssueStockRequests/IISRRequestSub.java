package MarketEntities.Subscribing.IssueStockRequests;

import MarketEntities.Subscribing.Subscription;
import Model.IssueStockRequest;

import java.util.List;

/**
 * Created by Felix on 16.04.2015.
 */
public interface IISRRequestSub extends Subscription {

    void pushNewISRs(List<IssueStockRequest> newISRs);

}
