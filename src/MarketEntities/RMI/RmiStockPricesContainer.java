package MarketEntities.RMI;

import MarketEntities.StockPricesContainer;
import MarketEntities.Subscribing.ASubManager;
import MarketEntities.Subscribing.IRmiCallback;
import Model.Company;
import Model.MarketValue;
import RMIServer.EntityProviders.IStockPricesProvider;
import Service.ConnectionError;
import Util.Container;
import Util.RmiUtil;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Felix on 25.04.2015.
 */
public class RmiStockPricesContainer extends StockPricesContainer{

    private IStockPricesProvider spContainer;
    private Set<IRmiCallback<MarketValue>> callbacks;

    public RmiStockPricesContainer() {
        spContainer = (IStockPricesProvider) RmiUtil.getContainer(Container.STOCK_PRICES);
        callbacks = new HashSet<>();
    }

    @Override
    public void addOrUpdateMarketValue(MarketValue marketValue, String transactionId) throws ConnectionError {
        try {
            spContainer.addOrUpdateMarketValue(marketValue, transactionId);
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public MarketValue getMarketValue(Company comp, String transactionId) throws ConnectionError {
        try {
            return spContainer.getMarketValue(comp, transactionId);
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public List<MarketValue> getAll(String transactionId) throws ConnectionError {
        try {
            return spContainer.getAll(transactionId);
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public void subscribe(ASubManager subscriber, String transactionId) throws ConnectionError {
        System.out.println("Subscription SP");
        IRmiCallback<MarketValue> rmiSub = (IRmiCallback<MarketValue>)subscriber;
        try {
            UnicastRemoteObject.exportObject(rmiSub, 0);
            spContainer.subscribe(rmiSub);
        } catch (RemoteException e) {
            if (e.getMessage().contains("already exported")) { //Export only once
                try {
                    spContainer.subscribe(rmiSub);
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
            for (IRmiCallback<MarketValue> rmiSub : callbacks) {
                spContainer.unsubscribe(rmiSub);
                UnicastRemoteObject.unexportObject(rmiSub, true);
            }
        } catch (RemoteException e) {
            throw new ConnectionError(e);
        }

    }
}
