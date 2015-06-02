package RMIServer.EntityProviders.Impl;

import MarketEntities.Subscribing.IRmiCallback;
import Model.Company;
import Model.MarketValue;
import RMIServer.EntityProviders.IBrokerSupportProvider;
import RMIServer.EntityProviders.IStockPricesProvider;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Felix on 25.04.2015.
 */
public class StockPricesProvider implements IStockPricesProvider {


    private volatile Set<MarketValue> stockPrices;
    private Object lock;
    private Set<IRmiCallback<MarketValue>> callbacks;
    private IBrokerSupportProvider bsp;

    public StockPricesProvider(IBrokerSupportProvider bsp) {
        stockPrices = new HashSet<>();
        callbacks = new HashSet<>();
        lock = new Object();
        this.bsp = bsp;
    }

    @Override
    public void addOrUpdateMarketValue(MarketValue marketValue, String transactionId) throws RemoteException {
        synchronized (lock) {
            stockPrices.remove(marketValue);
            stockPrices.add(marketValue);
        }

        List<MarketValue> newMWs = new ArrayList<MarketValue>();
        newMWs.add(marketValue);
        if (marketValue.isPriceChanged()) {
            bsp.addNewStockPrices(newMWs);
            System.out.println(getClass().getSimpleName() + ": addOrUpdateMarketValue : PRICE AND AMOUNT : "+marketValue+": sizenow :"+stockPrices.size());
        } else {
            System.out.println(getClass().getSimpleName() + ": addOrUpdateMarketValue : JUST AMOUNT :"+marketValue+": sizenow :"+stockPrices.size());
        }
        for (IRmiCallback<MarketValue> callback : callbacks) {
            callback.newData(newMWs);
        }
    }

    @Override
    public MarketValue getMarketValue(String id, String transactionId) throws RemoteException {
        synchronized (lock) {
            for (MarketValue mw : stockPrices) {
                if (mw.getId().equals(id)) {
                    System.out.println(getClass().getSimpleName()+": getMarketValue "+mw);
                    return mw;
                }
            }
            System.out.println(getClass().getSimpleName()+": getMarketValue NULL");
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
    public List<MarketValue> getFonds(String transactionId) throws RemoteException {
        synchronized (lock) {
            List<MarketValue> result = new ArrayList<>();
            for (MarketValue mw: stockPrices) {
                if (!mw.isCompany()) {
                    result.add(mw);
                }
            }
            return result;
        }
    }

    @Override
    public List<MarketValue> getCompanies(String transactionId) throws RemoteException {
        synchronized (lock) {
            List<MarketValue> result = new ArrayList<>();
            for (MarketValue mw: stockPrices) {
                if (mw.isCompany()) {
                    result.add(mw);
                }
            }
            return result;
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
