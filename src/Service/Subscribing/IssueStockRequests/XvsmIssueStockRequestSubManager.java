package Service.Subscribing.IssueStockRequests;

import Model.IssueStockRequest;
import Service.Broker;
import Service.ConnectionError;
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
public class XvsmIssueStockRequestSubManager extends AIssueStockRequestSubManager implements NotificationListener {

    public XvsmIssueStockRequestSubManager(IIssueStockRequestSub subscription) {
        super(subscription);
    }

    @Override
    public void entryOperationFinished(Notification source, Operation operation, List<? extends Serializable> entries) {
        List<IssueStockRequest> newISRs = new ArrayList<>();
        for (Serializable e: entries) {
            IssueStockRequest isr = (IssueStockRequest)((Entry) e).getValue();
            newISRs.add(isr);
        }
        subscription.pushNewISRs(newISRs);
    }
}
