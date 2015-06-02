package RMIServer.EntityProviders;

import Model.IssueRequest;
import Model.IssueStockRequest;
import MarketEntities.Subscribing.IRmiCallback;
import RMIServer.ICallbackDummy;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by Felix on 21.04.2015.
 */
public interface IIssueRequestsProvider extends IProvider {

    void addIssueRequest(IssueRequest ir, String transactionId) throws RemoteException;

    List<IssueRequest> takeIssueRequests(String transactionId, ICallbackDummy callerDummy) throws RemoteException;

    void subscribe(IRmiCallback<IssueRequest> callback) throws RemoteException;

    void unsubscribe(IRmiCallback<IssueRequest> callback) throws RemoteException;

}
