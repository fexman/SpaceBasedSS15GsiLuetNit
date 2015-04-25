package RMIServer.EntityProviders.Impl;

import Model.IssueStockRequest;
import RMIServer.EntityProviders.IISRContainerProvider;
import MarketEntities.Subscribing.IRmiCallback;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Felix on 21.04.2015.
 */
public class ISRContainerProvider implements IISRContainerProvider {

    private List<IssueStockRequest> isrs;
    private Object lock;
    private Set<IRmiCallback<IssueStockRequest>> callbacks;

    public ISRContainerProvider() {
        isrs = new ArrayList<>();
        callbacks = new HashSet<>();
        lock = new Object();
    }

    public void addIssueStocksRequest(IssueStockRequest isr, String transactionId) throws RemoteException {
        synchronized (lock) {
            System.out.println("Got isr: " + isr);
            isrs.add(isr);

            //Callback
            List<IssueStockRequest> newIsrs = new ArrayList<>();
            newIsrs.add(isr);
            for (IRmiCallback<IssueStockRequest> callback: callbacks) {
                callback.newData(newIsrs);
            }
        }
    }

    public List<IssueStockRequest> takeIssueStockRequests(String transactionId) throws RemoteException {
        synchronized (lock) {
            List<IssueStockRequest> returnVal = new ArrayList<>(isrs);
            isrs.clear();
            return returnVal;
        }
    }

    public void subscribe(IRmiCallback<IssueStockRequest> callback) throws RemoteException {
        callbacks.add(callback);
    }

    @Override
    public void unsubscribe(IRmiCallback<IssueStockRequest> callback) throws RemoteException {
        callbacks.remove(callback);
    }
}
