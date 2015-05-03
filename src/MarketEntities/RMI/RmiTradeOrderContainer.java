package MarketEntities.RMI;

import MarketEntities.Subscribing.ASubManager;
import MarketEntities.TradeOrderContainer;
import Model.TradeOrder;
import RMIServer.EntityProviders.ITradeOrderProvider;
import MarketEntities.Subscribing.IRmiCallback;
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

    private ITradeOrderProvider toContainer;
    private Set<IRmiCallback<TradeOrder>> callbacks;

    public RmiTradeOrderContainer() {
        toContainer = (ITradeOrderProvider) RmiUtil.getContainer(Container.TRADE_ORDERS);
        callbacks = new HashSet<>();
    }

    @Override
    public void addOrUpdateOrder(TradeOrder order, String transactionId) throws ConnectionError {
        try {
            toContainer.addOrUpdateOrder(order, transactionId);
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public List<TradeOrder> getOrders(TradeOrder order, String transactionId) throws ConnectionError {
        try {
            return toContainer.getOrders(order, transactionId);
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public TradeOrder takeOrder(TradeOrder tradeOrder, String transactionId) throws ConnectionError {
        //TODO
        return null;
    }

    @Override
    public List<TradeOrder> getAllOrders(String transactionId) throws ConnectionError {
        try {
            return toContainer.getAllOrders(transactionId);
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public void subscribe(ASubManager subscriber, String transactionId) throws ConnectionError {
        IRmiCallback<TradeOrder> rmiSub = (IRmiCallback<TradeOrder>)subscriber;
        try {
            UnicastRemoteObject.exportObject(rmiSub, 0);
            toContainer.subscribe(rmiSub);
        } catch (RemoteException e) {
            if (e.getMessage().contains("already exported")) { //Export only once
                try {
                    toContainer.subscribe(rmiSub);
                    return;
                } catch (RemoteException e1) {
                    throw new ConnectionError(e);
                }
            }
            throw new ConnectionError(e);
        }
    }

    public void removeSubscriptions() throws ConnectionError{
        try {
            for (IRmiCallback<TradeOrder> rmiSub : callbacks) {
                toContainer.unsubscribe(rmiSub);
                UnicastRemoteObject.unexportObject(rmiSub, true);
            }
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
    }

}
