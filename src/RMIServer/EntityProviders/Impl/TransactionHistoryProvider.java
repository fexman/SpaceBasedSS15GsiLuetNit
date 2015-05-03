package RMIServer.EntityProviders.Impl;

import MarketEntities.Subscribing.IRmiCallback;
import Model.HistoryEntry;
import RMIServer.EntityProviders.ITransactionHistoryProvider;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by j0h1 on 02.05.2015.
 */
public class TransactionHistoryProvider implements ITransactionHistoryProvider {

    private Set<HistoryEntry> historyEntries;
    private Object lock;
    private Set<IRmiCallback<HistoryEntry>> callbacks;

    @Override
    public void addHistoryEntry(HistoryEntry historyEntry, String transactionId) throws RemoteException {
        synchronized (lock) {
            historyEntries.add(historyEntry);
        }

        List<HistoryEntry> newHistoryEntries = new ArrayList<>();
        newHistoryEntries.add(historyEntry);
        for (IRmiCallback<HistoryEntry> callback : callbacks) {
            callback.newData(newHistoryEntries);
        }
    }

    @Override
    public List<HistoryEntry> getTransactionHistory(String transactionId) throws RemoteException {
        return new ArrayList<>(historyEntries);
    }

    @Override
    public void subscribe(IRmiCallback<HistoryEntry> callback) throws RemoteException {
        callbacks.add(callback);
    }

    @Override
    public void unsubscribe(IRmiCallback<HistoryEntry> callback) throws RemoteException {
        callbacks.remove(callback);
    }
}
