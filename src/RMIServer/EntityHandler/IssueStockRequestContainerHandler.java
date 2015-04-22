package RMIServer.EntityHandler;

import MarketEntities.IssueStockRequestContainer;
import Model.IssueStockRequest;
import Service.ConnectionError;
import Service.Subscribing.IssueStockRequests.AIssueStockRequestSubManager;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 21.04.2015.
 */
public class IssueStockRequestContainerHandler implements IIssueStockRequestContainerHandler {

    private List<IssueStockRequest> isrs;
    private Object lock;

    public IssueStockRequestContainerHandler() {
        isrs = new ArrayList<IssueStockRequest>();
        lock = new Object();
    }

    public void addIssueStocksRequest(IssueStockRequest isr, String transactionId) throws RemoteException {
        synchronized (lock) {
            System.out.println("Got isr: "+isr);
            isrs.add(isr);
        }
    }

    public List<IssueStockRequest> takeIssueStockRequests(String transactionId) throws RemoteException {
        synchronized (lock) {
            List<IssueStockRequest> returnVal = new ArrayList<>(isrs);
            isrs.clear();
            return returnVal;
        }
    }

    public void subscribe(AIssueStockRequestSubManager subscriber, String transactionId) throws RemoteException {

    }
}
