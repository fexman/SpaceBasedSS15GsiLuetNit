package MarketEntities.Subscribing.IssueStockRequests;

import Model.IssueStockRequest;
import MarketEntities.Subscribing.IRmiCallback;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * Created by Felix on 23.04.2015.
 */
public class RmiISRSubManager extends AISRSubManager implements IRmiCallback<IssueStockRequest> {


    public RmiISRSubManager(IISRRequestSub subscription) throws RemoteException {
        super(subscription);
        UnicastRemoteObject.exportObject(this, 0);
    }

    @Override
    public void newData(List<IssueStockRequest> newData) throws RemoteException {
        subscription.pushNewISRs(newData);
    }
}
