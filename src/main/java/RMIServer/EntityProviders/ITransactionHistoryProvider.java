package RMIServer.EntityProviders;

import MarketEntities.Subscribing.IRmiCallback;
import Model.HistoryEntry;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by j0h1 on 02.05.2015.
 */
public interface ITransactionHistoryProvider extends IProvider {

    void addHistoryEntry(HistoryEntry historyEntry, String transactionId) throws RemoteException;

    List<HistoryEntry> getTransactionHistory(String transactionId) throws RemoteException;

    void subscribe(IRmiCallback<HistoryEntry> callback) throws RemoteException;

    void unsubscribe(IRmiCallback<HistoryEntry> callback) throws RemoteException;
}
