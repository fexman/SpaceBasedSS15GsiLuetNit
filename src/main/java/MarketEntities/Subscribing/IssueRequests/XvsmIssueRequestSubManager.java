package MarketEntities.Subscribing.IssueRequests;

import Model.IssueRequest;
import Model.IssueStockRequest;
import org.mozartspaces.core.Entry;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.Operation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 16.04.2015.
 */
public class XvsmIssueRequestSubManager extends AIssueRequestSubManager implements NotificationListener {

    public XvsmIssueRequestSubManager(IIssueRequestSub subscription) {
        super(subscription);
    }

    @Override
    public void entryOperationFinished(Notification source, Operation operation, List<? extends Serializable> entries) {
        List<IssueRequest> newIRs = new ArrayList<>();
        for (Serializable e: entries) {
            IssueRequest ir = (IssueRequest)((Entry) e).getValue();
            newIRs.add(ir);
        }
        subscription.pushNewIRs(newIRs);
    }
}
