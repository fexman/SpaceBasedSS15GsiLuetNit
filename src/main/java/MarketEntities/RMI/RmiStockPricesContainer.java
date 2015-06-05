package MarketEntities.RMI;

import MarketEntities.StockPricesContainer;
import MarketEntities.Subscribing.ASubManager;
import MarketEntities.Subscribing.IRmiCallback;
import Model.Company;
import Model.MarketValue;
import RMIServer.EntityProviders.IStockPricesProvider;
import Service.ConnectionErrorException;
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
    private RmiUtil util;

    public RmiStockPricesContainer(RmiUtil util) {
        this.util = util;
        spContainer = (IStockPricesProvider) util.getContainer(Container.STOCK_PRICES);
        callbacks = new HashSet<>();
    }

    @Override
    public void addOrUpdateMarketValue(MarketValue marketValue, String transactionId) throws ConnectionErrorException {
        try {
            spContainer.addOrUpdateMarketValue(marketValue, transactionId);
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public MarketValue getMarketValue(String id, String transactionId) throws ConnectionErrorException {
        try {
            return spContainer.getMarketValue(id, transactionId);
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public List<MarketValue> getAll(String transactionId) throws ConnectionErrorException {
        try {
            return spContainer.getAll(transactionId);
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public List<MarketValue> getFonds(String transactionId) throws ConnectionErrorException {
        try {
            return spContainer.getFonds(transactionId);
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public List<MarketValue> getCompanies(String transactionId) throws ConnectionErrorException {
        try {
            return spContainer.getCompanies(transactionId);
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }
    }

    @Override
    public void subscribe(ASubManager subscriber, String transactionId) throws ConnectionErrorException {
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
                    throw new ConnectionErrorException(e);
                }
            }
            throw new ConnectionErrorException(e);
        }
    }

    public void removeSubscriptions() throws ConnectionErrorException {
        try {
            for (IRmiCallback<MarketValue> rmiSub : callbacks) {
                spContainer.unsubscribe(rmiSub);
                UnicastRemoteObject.unexportObject(rmiSub, true);
            }
        } catch (RemoteException e) {
            throw new ConnectionErrorException(e);
        }

    }
}
