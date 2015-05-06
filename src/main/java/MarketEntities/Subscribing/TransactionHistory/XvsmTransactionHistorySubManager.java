package MarketEntities.Subscribing.TransactionHistory;

import Model.HistoryEntry;
import org.mozartspaces.core.Entry;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.Operation;

import java.io.Serializable;
import java.util.List;

/**
 * Created by j0h1 on 28.04.2015.
 */
public class XvsmTransactionHistorySubManager extends ATransactionHistorySubManager implements NotificationListener{

    public XvsmTransactionHistorySubManager(ITransactionHistorySub subscription) {
            super(subscription);
}

    @Override
    public void entryOperationFinished(Notification notification, Operation operation, List<? extends Serializable> historyEntries) {
        for (Serializable e: historyEntries) {
            HistoryEntry historyEntry = (HistoryEntry)((Entry) e).getValue();
            subscription.pushNewHistoryEntry(historyEntry);
        }
    }

}
