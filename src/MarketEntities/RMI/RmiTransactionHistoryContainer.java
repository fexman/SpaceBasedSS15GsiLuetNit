package MarketEntities.RMI;

import MarketEntities.Subscribing.ASubManager;
import MarketEntities.Subscribing.IRmiCallback;
import MarketEntities.TransactionHistoryContainer;
import Model.HistoryEntry;
import RMIServer.EntityProviders.ITransactionHistoryProvider;
import Service.ConnectionError;
import Util.Container;
import Util.RmiUtil;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by j0h1 on 02.05.2015.
 */
public class RmiTransactionHistoryContainer extends TransactionHistoryContainer {

    private ITransactionHistoryProvider transactionHistoryContainer;
    private Set<IRmiCallback<HistoryEntry>> callbacks;

    public RmiTransactionHistoryContainer() {
        transactionHistoryContainer = (ITransactionHistoryProvider) RmiUtil.getContainer(Container.TRANSACTION_HISTORY);
        callbacks = new HashSet<>();
    }

    @Override
    public void addHistoryEntry(HistoryEntry historyEntry, String transactionId) throws ConnectionError {
        try {
            transactionHistoryContainer.addHistoryEntry(historyEntry, transactionId);
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public List<HistoryEntry> getTransactionHistory(String transactionId) throws ConnectionError {
        try {
            return transactionHistoryContainer.getTransactionHistory(transactionId);
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public void subscribe(ASubManager subscriber, String transactionId) throws ConnectionError {
        IRmiCallback<HistoryEntry> rmiSub = (IRmiCallback<HistoryEntry>) subscriber;
        try {
            UnicastRemoteObject.exportObject(rmiSub, 0);
            transactionHistoryContainer.subscribe(rmiSub);
        } catch (RemoteException e) {
            if (e.getMessage().contains("already exported")) { //Export only once
                try {
                    transactionHistoryContainer.subscribe(rmiSub);
                    return;
                } catch (RemoteException e1) {
                    throw new ConnectionError(e);
                }
            }
            throw new ConnectionError(e);
        }
    }

    public void removeSubscriptions() throws ConnectionError{
        try {
            for (IRmiCallback<HistoryEntry> rmiSub : callbacks) {
                transactionHistoryContainer.unsubscribe(rmiSub);
                UnicastRemoteObject.unexportObject(rmiSub, true);
            }
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
    }

}