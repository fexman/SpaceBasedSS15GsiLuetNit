package RMIServer.EntityProviders;

import Model.IssueStockRequest;
import MarketEntities.Subscribing.IRmiCallback;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by Felix on 21.04.2015.
 */
public interface IISRContainerProvider extends IProvider {

    void addIssueStocksRequest(IssueStockRequest isr, String transactionId) throws RemoteException;

    List<IssueStockRequest> takeIssueStockRequests(String transactionId) throws RemoteException;

    void subscribe(IRmiCallback<IssueStockRequest> callback) throws RemoteException;

    void unsubscribe(IRmiCallback<IssueStockRequest> callback) throws RemoteException;

}
