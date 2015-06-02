package MarketEntities.Subscribing.IssueRequests;

import MarketEntities.Subscribing.Subscription;
import Model.IssueRequest;

import java.util.List;

/**
 * Created by Felix on 16.04.2015.
 */
public interface IIssueRequestSub extends Subscription {

    void pushNewIRs(List<IssueRequest> newISRs);

}
