package MarketEntities.RMI;

import MarketEntities.IssueStockRequestContainer;
import MarketEntities.Subscribing.ASubManager;
import Model.IssueStockRequest;
import RMIServer.EntityHandler.IIssueStockRequestContainerHandler;
import RMIServer.RmiCallback;
import Service.ConnectionError;
import Util.Container;
import Util.RmiUtil;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Felix on 22.04.2015.
 */
public class RmiIssueStockRequestContainer extends IssueStockRequestContainer {

    private IIssueStockRequestContainerHandler isrHandler;
    private Set<RmiCallback<IssueStockRequest>> callbacks;

    public RmiIssueStockRequestContainer() {
        isrHandler = (IIssueStockRequestContainerHandler)RmiUtil.getHandler(Container.ISSUED_STOCK_REQUESTS);
        callbacks = new HashSet<>();
    }

    @Override
    public void addIssueStocksRequest(IssueStockRequest isr, String transactionId) throws ConnectionError {
        try {
            isrHandler.addIssueStocksRequest(isr,transactionId);
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public List<IssueStockRequest> takeIssueStockRequests(String transactionId) throws ConnectionError {
        try {
            return isrHandler.takeIssueStockRequests(transactionId);
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public void subscribe(ASubManager subscriber, String transactionId) throws ConnectionError {
        RmiCallback<IssueStockRequest> rmiSub = (RmiCallback<IssueStockRequest>)subscriber;
        try {
            UnicastRemoteObject.exportObject(rmiSub,0);
            isrHandler.subscribe(rmiSub);
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
    }

    public void removeSubscriptions() throws ConnectionError{
        try {
            for (RmiCallback<IssueStockRequest> rmiSub : callbacks) {
                isrHandler.unsubscribe(rmiSub);
                UnicastRemoteObject.unexportObject(rmiSub, true);
            }
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }

    }
}
