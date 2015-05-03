package RMIServer.EntityProviders;

import Model.TradeOrder;
import MarketEntities.Subscribing.IRmiCallback;
import RMIServer.ICallbackDummy;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by Felix on 22.04.2015.
 */
public interface ITradeOrderProvider extends IProvider {

    void addOrUpdateOrder(TradeOrder order, String transactionId) throws RemoteException;

    List<TradeOrder> getOrders(TradeOrder order, String transactionId) throws RemoteException;

    List<TradeOrder> getAllOrders(String transactionId) throws RemoteException;

    TradeOrder takeOrder(TradeOrder tradeOrder, ICallbackDummy callerDummy, String transactionId) throws RemoteException;

    void subscribe(IRmiCallback<TradeOrder> callback) throws RemoteException;

    void unsubscribe(IRmiCallback<TradeOrder> callback) throws RemoteException;
}
