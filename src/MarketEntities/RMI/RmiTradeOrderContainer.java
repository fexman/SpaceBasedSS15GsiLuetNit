package MarketEntities.RMI;

import MarketEntities.Subscribing.ASubManager;
import MarketEntities.TradeOrderContainer;
import Model.TradeOrder;
import RMIServer.EntityHandler.ITradeOrderContainerHandler;
import RMIServer.RmiCallback;
import Service.ConnectionError;
import Util.Container;
import Util.RmiUtil;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Felix on 23.04.2015.
 */
public class RmiTradeOrderContainer extends TradeOrderContainer {

    private ITradeOrderContainerHandler toHandler;
    private Set<RmiCallback<TradeOrder>> callbacks;

    public RmiTradeOrderContainer() {
        toHandler = (ITradeOrderContainerHandler) RmiUtil.getHandler(Container.TRADE_ORDERS);
        callbacks = new HashSet<>();
    }

    @Override
    public void addOrUpdateOrder(TradeOrder order, String transactionId) throws ConnectionError {
        try {
            toHandler.addOrUpdateOrder(order,transactionId);
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public List<TradeOrder> getOrders(TradeOrder order, String transactionId) throws ConnectionError {
        try {
            return toHandler.getOrders(order, transactionId);
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public List<TradeOrder> getAllOrders(String transactionId) throws ConnectionError {
        try {
            return toHandler.getAllOrders(transactionId);
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public void subscribe(ASubManager subscriber, String transactionId) throws ConnectionError {
        RmiCallback<TradeOrder> rmiSub = (RmiCallback<TradeOrder>)subscriber;
        try {
            UnicastRemoteObject.exportObject(rmiSub, 0);
            toHandler.subscribe(rmiSub);
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
    }

    public void removeSubscriptions() throws ConnectionError{
        try {
            for (RmiCallback<TradeOrder> rmiSub : callbacks) {
                toHandler.unsubscribe(rmiSub);
                UnicastRemoteObject.unexportObject(rmiSub, true);
            }
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }

    }
}
