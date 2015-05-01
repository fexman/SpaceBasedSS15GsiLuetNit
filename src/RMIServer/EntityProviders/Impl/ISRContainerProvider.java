package RMIServer.EntityProviders.Impl;

import Model.IssueStockRequest;
import RMIServer.EntityProviders.IISRContainerProvider;
import MarketEntities.Subscribing.IRmiCallback;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Felix on 21.04.2015.
 */
public class ISRContainerProvider implements IISRContainerProvider {

    private volatile List<IssueStockRequest> isrs;
    private Object lock;

    private Set<IRmiCallback<IssueStockRequest>> callbacks;

    public ISRContainerProvider() {
        isrs = new ArrayList<>();
        callbacks = new HashSet<>();
        lock = new Object();
    }

    public void addIssueStocksRequest(IssueStockRequest isr, String transactionId) throws RemoteException {

        synchronized (lock) {
            isrs.add(isr);
            System.out.println("Added: "+isr);
            synchronized (isrs) {
                isrs.notifyAll(); //Wake up one Thread waiting for resources
            }
        }

        //Callback
        List<IssueStockRequest> newIsrs = new ArrayList<>();
        newIsrs.add(isr);
        for (IRmiCallback<IssueStockRequest> callback: callbacks) {
            callback.newData(newIsrs);
        }
        System.out.println("Isrs-size:"+isrs.size());
    }

    public List<IssueStockRequest> takeIssueStockRequests(String transactionId) throws RemoteException {

        synchronized (isrs) {
            while (isrs.isEmpty()) {
                try {
                    isrs.wait(); //Wait for change in Resources
                } catch (InterruptedException e) {
                }
            }
        }

        synchronized (lock) { //Only one at a time
            List<IssueStockRequest> returnVal = new ArrayList<>(isrs);
            isrs = new ArrayList<>();
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

    public String toString() {
        synchronized (lock) {
            String info = "";
            info += "========= ISR CONTAINER ========\n";
            info += "callbacks: "+callbacks.size()+"\n";
            info += "entries: "+isrs.size()+"\n";
            info += "================================\n";
            int counter = 1;
            if (!isrs.isEmpty()) {
                for (IssueStockRequest isr : isrs) {
                    info += "[" + counter + "]: " + isr.toString() + "\n";
                    counter++;
                }
                info += "================================\n";
            }
            return info;
        }
    }
}
