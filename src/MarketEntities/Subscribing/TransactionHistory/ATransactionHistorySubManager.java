package MarketEntities.Subscribing.TransactionHistory;

import MarketEntities.Subscribing.ASubManager;
import Model.HistoryEntry;

/**
 * Created by j0h1 on 28.04.2015.
 */
public class ATransactionHistorySubManager extends ASubManager<ITransactionHistorySub> {

    public ATransactionHistorySubManager(ITransactionHistorySub subscription) {
        super(subscription);
    }

}
