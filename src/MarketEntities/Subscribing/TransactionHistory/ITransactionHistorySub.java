package MarketEntities.Subscribing.TransactionHistory;

import MarketEntities.Subscribing.Subscription;
import Model.HistoryEntry;

/**
 * Created by j0h1 on 28.04.2015.
 */
public interface ITransactionHistorySub extends Subscription {

    void pushNewHistoryEntry(HistoryEntry historyEntry);

}
