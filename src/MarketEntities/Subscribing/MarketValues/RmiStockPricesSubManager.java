package MarketEntities.Subscribing.MarketValues;

import MarketEntities.Subscribing.IRmiCallback;
import Model.MarketValue;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * Created by Felix on 25.04.2015.
 */
public class RmiStockPricesSubManager extends AStockPricesSubManager implements IRmiCallback<MarketValue> {

    public RmiStockPricesSubManager(IStockPricesSub subscription) throws RemoteException {
        super(subscription);
        UnicastRemoteObject.exportObject(this, 0);
    }


    @Override
    public void newData(List<MarketValue> newData) throws RemoteException {
        subscription.pushNewMarketValues(newData);
    }
}
