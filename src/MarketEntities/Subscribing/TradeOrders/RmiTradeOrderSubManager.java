package MarketEntities.Subscribing.TradeOrders;

import Model.TradeOrder;
import RMIServer.RmiCallback;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * Created by Felix on 23.04.2015.
 */
public class RmiTradeOrderSubManager extends ATradeOrderSubManager implements RmiCallback<TradeOrder> {

    public RmiTradeOrderSubManager(ITradeOrderSub subscription) throws RemoteException {
        super(subscription);
        UnicastRemoteObject.exportObject(this, 0);
    }

    @Override
    public void newData(List<TradeOrder> newData) throws RemoteException {
        subscription.pushNewTradeOrders(newData);
    }
}
