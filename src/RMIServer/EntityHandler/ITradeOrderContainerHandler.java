package RMIServer.EntityHandler;

import Model.TradeOrder;
import RMIServer.RmiCallback;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by Felix on 22.04.2015.
 */
public interface ITradeOrderContainerHandler extends IHandler {


    abstract void addOrUpdateOrder(TradeOrder order, String transactionId) throws RemoteException;

    abstract List<TradeOrder> getOrders(TradeOrder order, String transactionId) throws RemoteException;

    abstract List<TradeOrder> getAllOrders(String transactionId) throws RemoteException;

    void subscribe(RmiCallback<TradeOrder> callback) throws RemoteException;

    void unsubscribe(RmiCallback<TradeOrder> callback) throws RemoteException;
}
