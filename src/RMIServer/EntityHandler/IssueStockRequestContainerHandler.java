package RMIServer.EntityHandler;

import Model.IssueStockRequest;
import RMIServer.RmiCallback;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Felix on 21.04.2015.
 */
public class IssueStockRequestContainerHandler implements IIssueStockRequestContainerHandler {

    private List<IssueStockRequest> isrs;
    private Object lock;
    private Set<RmiCallback<IssueStockRequest>> callbacks;

    public IssueStockRequestContainerHandler() {
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
            for (RmiCallback<IssueStockRequest> callback: callbacks) {
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

    public void subscribe(RmiCallback<IssueStockRequest> callback) throws RemoteException {
        callbacks.add(callback);
    }

    @Override
    public void unsubscribe(RmiCallback<IssueStockRequest> callback) throws RemoteException {
        callbacks.remove(callback);
    }
}
