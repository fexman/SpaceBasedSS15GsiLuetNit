package RMIServer.EntityProviders.Impl;

import MarketEntities.Subscribing.IRmiCallback;
import Model.Company;
import Model.MarketValue;
import RMIServer.EntityProviders.IStockPricesProvider;
import Service.ConnectionError;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Felix on 25.04.2015.
 */
public class StockPricesProvider implements IStockPricesProvider {


    private Set<MarketValue> stockPrices;
    private Object lock;
    private Set<IRmiCallback<MarketValue>> callbacks;

    public StockPricesProvider() {
        stockPrices = new HashSet<>();
        callbacks = new HashSet<>();
        lock = new Object();
    }

    @Override
    public void addOrUpdateMarketValue(MarketValue marketValue, String transactionId) throws RemoteException {
        synchronized (lock) {
            stockPrices.add(marketValue);
        }

        List<MarketValue> newMWs = new ArrayList<MarketValue>();
        newMWs.add(marketValue);
        for (IRmiCallback<MarketValue> callback : callbacks) {
            callback.newData(newMWs);
        }

    }

    @Override
    public MarketValue getMarketValue(Company comp, String transactionId) throws RemoteException {
        synchronized (lock) {
            for (MarketValue mw : stockPrices) {
                if (mw.getCompany().equals(comp)) {
                    return mw;
                }
            }
            return null;
        }
    }

    @Override
    public List<MarketValue> getAll(String transactionId) throws RemoteException {
        synchronized (lock) {
            return new ArrayList<>(stockPrices);
        }
    }

    @Override
    public void subscribe(IRmiCallback<MarketValue> callback) throws RemoteException {
        callbacks.add(callback);
    }

    @Override
    public void unsubscribe(IRmiCallback<MarketValue> callback) throws RemoteException {
        callbacks.remove(callback);
    }

    public String toString() {
        String info = "";
        info += "===== STOCKPRICES CONTAINER ====\n";
        info += "callbacks: "+callbacks.size()+"\n";
        info += "entries: "+stockPrices.size()+"\n";
        info += "================================\n";
        int counter = 1;
        if (!stockPrices.isEmpty()) {
            for (MarketValue mw : stockPrices) {
                info += "[" + counter + "]: " + mw.toString() + "\n";
                counter++;
            }
            info += "================================\n";
        }
        return info;
    }

}
