package RMIServer.EntityProviders;

import Model.MarketValue;
import Model.TradeOrder;
import RMIServer.ICallbackDummy;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by j0h1 on 06.05.2015.
 */
public interface IBrokerSupportProvider extends IProvider {

    abstract void addNewTradeOrders(List<TradeOrder> orders) throws RemoteException;

    abstract void addNewStockPrices(List<MarketValue> stockPrices) throws RemoteException;

    abstract List<TradeOrder> takeNewTradeOrders(String transactionId, ICallbackDummy caller) throws RemoteException;

    abstract List<MarketValue> takeNewStockPrices(String transactionId, ICallbackDummy caller) throws RemoteException;

}
