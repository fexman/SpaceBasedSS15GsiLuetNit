package RMIServer.EntityProviders;

import MarketEntities.Subscribing.IRmiCallback;
import Model.Company;
import Model.MarketValue;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by Felix on 25.04.2015.
 */
public interface IStockPricesProvider extends IProvider {

    void addOrUpdateMarketValue(MarketValue marketValue, String transactionId) throws RemoteException;

    MarketValue getMarketValue(String id, String transactionId) throws RemoteException;

    List<MarketValue> getAll(String transactionId) throws RemoteException;

    void subscribe(IRmiCallback<MarketValue> callback) throws RemoteException;

    void unsubscribe(IRmiCallback<MarketValue> callback) throws RemoteException;
}
