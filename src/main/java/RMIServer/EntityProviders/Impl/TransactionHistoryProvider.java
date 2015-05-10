package RMIServer.EntityProviders.Impl;

import MarketEntities.Subscribing.IRmiCallback;
import Model.HistoryEntry;
import RMIServer.EntityProviders.ITransactionHistoryProvider;

import java.rmi.RemoteException;
import java.util.*;

/**
 * Created by j0h1 on 02.05.2015.
 */
public class TransactionHistoryProvider implements ITransactionHistoryProvider {

    private List<HistoryEntry> historyEntries;
    private Object lock;
    private Set<IRmiCallback<HistoryEntry>> callbacks;

    public TransactionHistoryProvider() {
        historyEntries = new ArrayList<>();
        callbacks = new HashSet<>();
        lock = new Object();
    }

    @Override
    public void addHistoryEntry(HistoryEntry historyEntry, String transactionId) throws RemoteException {
        synchronized (lock) {
            historyEntry.setTransactionId(UUID.randomUUID().toString());
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
        return historyEntries;
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
