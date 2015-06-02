package MarketEntities.RMI;

import MarketEntities.IssueRequestContainer;
import MarketEntities.Subscribing.ASubManager;
import Model.IssueRequest;
import RMIServer.CallbackDummy;
import RMIServer.EntityProviders.IIssueRequestsProvider;
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
public class RmiIssueRequestContainer extends IssueRequestContainer {

    private IIssueRequestsProvider irContainer;
    private Set<IRmiCallback<IssueRequest>> callbacks;

    public RmiIssueRequestContainer() {
        irContainer = (IIssueRequestsProvider)RmiUtil.getContainer(Container.ISSUED_REQUESTS);
        callbacks = new HashSet<>();
    }

    @Override
    public void addIssueRequest(IssueRequest ir, String transactionId) throws ConnectionErrorException {
        try {
            irContainer.addIssueRequest(ir, transactionId);
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public List<IssueRequest> takeIssueRequests(String transactionId) throws ConnectionErrorException {
        try {
            ICallbackDummy callerDummy = new CallbackDummy();
            UnicastRemoteObject.exportObject(callerDummy,0);
            return irContainer.takeIssueRequests(transactionId, callerDummy);
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public void subscribe(ASubManager subscriber, String transactionId) throws ConnectionErrorException {
        IRmiCallback<IssueRequest> rmiSub = (IRmiCallback<IssueRequest>)subscriber;
        try {
            UnicastRemoteObject.exportObject(rmiSub,0);
            irContainer.subscribe(rmiSub);
        } catch (RemoteException e) {
            if (e.getMessage().contains("already exported")) { //Export only once
                try {
                    irContainer.subscribe(rmiSub);
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
            for (IRmiCallback<IssueRequest> rmiSub : callbacks) {
                irContainer.unsubscribe(rmiSub);
                UnicastRemoteObject.unexportObject(rmiSub, true);
            }
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }

    }
}
