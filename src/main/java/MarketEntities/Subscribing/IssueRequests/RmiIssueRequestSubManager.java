package MarketEntities.Subscribing.IssueRequests;

import Model.IssueRequest;
import Model.IssueStockRequest;
import MarketEntities.Subscribing.IRmiCallback;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * Created by Felix on 23.04.2015.
 */
public class RmiIssueRequestSubManager extends AIssueRequestSubManager implements IRmiCallback<IssueRequest> {


    public RmiIssueRequestSubManager(IIssueRequestSub subscription) throws RemoteException {
        super(subscription);
        UnicastRemoteObject.exportObject(this, 0);
    }

    @Override
    public void newData(List<IssueRequest> newData) throws RemoteException {
        subscription.pushNewIRs(newData);
    }

}
