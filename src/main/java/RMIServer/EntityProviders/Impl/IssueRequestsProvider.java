package RMIServer.EntityProviders.Impl;

import Model.IssueRequest;
import Model.IssueStockRequest;
import RMIServer.EntityProviders.IIssueRequestsProvider;
import MarketEntities.Subscribing.IRmiCallback;
import RMIServer.ICallbackDummy;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Felix on 21.04.2015.
 */
public class IssueRequestsProvider implements IIssueRequestsProvider {

    private volatile List<IssueRequest> irs;
    private Object lock;

    private Set<IRmiCallback<IssueRequest>> callbacks;

    public IssueRequestsProvider() {
        irs = new ArrayList<>();
        callbacks = new HashSet<>();
        lock = new Object();
    }

    public void addIssueRequest(IssueRequest ir, String transactionId) throws RemoteException {

        synchronized (lock) {
            irs.add(ir);
            System.out.println(getClass().getSimpleName() + ": addIssueStocksRequest: " + ir);
            synchronized (irs) {
                irs.notifyAll(); //Wake up one Thread waiting for resources
            }
        }

        List<IssueRequest> newIrs = new ArrayList<>();
        newIrs.add(ir);
        for (IRmiCallback<IssueRequest> callback: callbacks) {
            callback.newData(newIrs);
        }
    }

    public List<IssueRequest> takeIssueRequests(String transactionId, ICallbackDummy callerDummy) throws RemoteException {

        repeat: while (true) {

            while (irs.isEmpty()) {
                try {
                    synchronized (irs) { //Only one at a time
                        irs.wait(); //Wait for change in Resources
                    }
                } catch (InterruptedException e) { }
            }

            synchronized (lock) {
                try {

                    if (irs.isEmpty()) {
                        continue repeat;
                    }

                    callerDummy.testConnection();
                    System.out.println(getClass().getSimpleName() + ": takeIssueStockRequests");
                    List<IssueRequest> returnVal = new ArrayList<>(irs);
                    irs = new ArrayList<>();
                    return returnVal;
                } catch (RemoteException e) {
                    System.out.println("That did not work out. :(");
                    return null;
                }
            }
        }


    }

    @Override
    public void subscribe(IRmiCallback<IssueRequest> callback) throws RemoteException {
        callbacks.add(callback);
    }

    @Override
    public void unsubscribe(IRmiCallback<IssueRequest> callback) throws RemoteException {
        callbacks.remove(callback);
    }

    public String toString() {
        synchronized (lock) {
            String info = "";
            info += "========= ISR CONTAINER ========\n";
            info += "callbacks: "+callbacks.size()+"\n";
            info += "entries: "+irs.size()+"\n";
            info += "================================\n";
            int counter = 1;
            if (!irs.isEmpty()) {
                for (IssueRequest ir : irs) {
                    info += "[" + counter + "]: " + ir.toString() + "\n";
                    counter++;
                }
                info += "================================\n";
            }
            return info;
        }
    }
}
