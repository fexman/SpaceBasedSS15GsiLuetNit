package MarketEntities.Subscribing.TradeOrders;

import Model.TradeOrder;
import MarketEntities.Subscribing.IRmiCallback;
import Service.ConnectionError;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * Created by Felix on 23.04.2015.
 */
public class RmiTradeOrderSubManager extends ATradeOrderSubManager implements IRmiCallback<TradeOrder> {

    public RmiTradeOrderSubManager(ITradeOrderSub subscription) throws RemoteException {
        super(subscription);
        UnicastRemoteObject.exportObject(this, 0);
    }

    @Override
    public void newData(List<TradeOrder> newData) throws RemoteException {
        for (TradeOrder tradeOrder : newData) {
                subscription.pushNewTradeOrders(tradeOrder);
        }
    }

}
