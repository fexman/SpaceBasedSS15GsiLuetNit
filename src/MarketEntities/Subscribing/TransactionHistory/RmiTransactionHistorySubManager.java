package MarketEntities.Subscribing.TransactionHistory;

import MarketEntities.Subscribing.IRmiCallback;
import Model.HistoryEntry;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * Created by j0h1 on 28.04.2015.
 */
public class RmiTransactionHistorySubManager extends ATransactionHistorySubManager implements IRmiCallback<HistoryEntry> {

    public RmiTransactionHistorySubManager(ITransactionHistorySub subscription) throws RemoteException {
        super(subscription);
        UnicastRemoteObject.exportObject(this, 0);
    }

    @Override
    public void newData(List<HistoryEntry> historyEntries) throws RemoteException {
        for (HistoryEntry e : historyEntries) {
            subscription.pushNewHistoryEntry(e);
        }
    }

}
