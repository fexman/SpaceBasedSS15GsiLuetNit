package RMIServer.EntityHandler;

import Model.IssueStockRequest;
import Service.Subscribing.IssueStockRequests.AIssueStockRequestSubManager;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by Felix on 21.04.2015.
 */
public interface IIssueStockRequestContainerHandler extends IHandler {

    void addIssueStocksRequest(IssueStockRequest isr, String transactionId) throws RemoteException;

    List<IssueStockRequest> takeIssueStockRequests(String transactionId) throws RemoteException;

    void subscribe(AIssueStockRequestSubManager subscriber, String transactionId) throws RemoteException;

}
