package MarketEntities.RMI;

import MarketEntities.ISRContainer;
import MarketEntities.Subscribing.ASubManager;
import Model.IssueStockRequest;
import RMIServer.CallbackDummy;
import RMIServer.EntityProviders.IISRContainerProvider;
import MarketEntities.Subscribing.IRmiCallback;
import RMIServer.ICallbackDummy;
import Service.ConnectionErrorException;
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
public class RmiISRContainer extends ISRContainer {

    private IISRContainerProvider isrContainer;
    private Set<IRmiCallback<IssueStockRequest>> callbacks;

    public RmiISRContainer() {
        isrContainer = (IISRContainerProvider)RmiUtil.getContainer(Container.ISSUED_STOCK_REQUESTS);
        callbacks = new HashSet<>();
    }

    @Override
    public void addIssueStocksRequest(IssueStockRequest isr, String transactionId) throws ConnectionErrorException {
        try {
            isrContainer.addIssueStocksRequest(isr,transactionId);
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public List<IssueStockRequest> takeIssueStockRequests(String transactionId) throws ConnectionErrorException {
        try {
            ICallbackDummy callerDummy = new CallbackDummy();
            UnicastRemoteObject.exportObject(callerDummy,0);
            return isrContainer.takeIssueStockRequests(transactionId, callerDummy);
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public void subscribe(ASubManager subscriber, String transactionId) throws ConnectionErrorException {
        IRmiCallback<IssueStockRequest> rmiSub = (IRmiCallback<IssueStockRequest>)subscriber;
        try {
            UnicastRemoteObject.exportObject(rmiSub,0);
            isrContainer.subscribe(rmiSub);
        } catch (RemoteException e) {
            if (e.getMessage().contains("already exported")) { //Export only once
                try {
                    isrContainer.subscribe(rmiSub);
                    return;
                } catch (RemoteException e1) {
                    throw new ConnectionErrorException(e);
                }
            }
            throw new ConnectionErrorException(e);
        }
    }

    public void removeSubscriptions() throws ConnectionErrorException {
        try {
            for (IRmiCallback<IssueStockRequest> rmiSub : callbacks) {
                isrContainer.unsubscribe(rmiSub);
                UnicastRemoteObject.unexportObject(rmiSub, true);
            }
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }

    }
}
