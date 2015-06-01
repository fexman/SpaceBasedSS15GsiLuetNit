package RMIServer.EntityProviders.Impl;

import MarketEntities.Subscribing.IRmiCallback;
import Model.*;
import RMIServer.EntityProviders.IDepotInvestorProvider;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;

/**
 * Created by j0h1 on 02.05.2015.
 */
public class DepotInvestorProvider implements IDepotInvestorProvider {

    private Investor investor;
    private Double budget;
    private HashMap<String, Integer> tradeObjects;
    private Set<String> knownCompanies;
    private Set<IRmiCallback<Serializable>> callbacks;
    private Object lock;

    public DepotInvestorProvider(Investor investor) {
        this.investor = investor;
        this.tradeObjects = new HashMap<>();
        this.knownCompanies = new HashSet<>();
        this.budget = 0.0;
        this.lock = new Object();
        this.callbacks = new HashSet<>();
    }

    @Override
    public Investor getInvestor() throws RemoteException {
        return investor;
    }

    @Override
    public double getBudget(String transactionId) throws RemoteException {
        synchronized (lock) {
            return budget;
        }
    }

    @Override
    public void setBudget(double amount, String transactionId) throws RemoteException {
        synchronized (lock) {
            this.budget = amount;
        }
        List<Serializable> sBudget = new ArrayList<>();
        sBudget.add((Serializable)budget);
        for (IRmiCallback<Serializable> callback: callbacks) {
            callback.newData(sBudget);
        }
    }

    @Override
    public List<TradeObject> takeTradeObjects(String toId, int amount, String transactionId) throws RemoteException {
        synchronized (lock) {
            List<TradeObject> removedTradeObjects = new ArrayList<>();

            // remove <amount> stocks from company <comp> from depot
            tradeObjects.put(toId, tradeObjects.get(toId) - amount);

            // create stocks to return
            for (int i = 0; i < amount; i++) {
                if (knownCompanies.contains(toId)) {
                    removedTradeObjects.add(new Stock(new Company(toId)));
                } else {
                    removedTradeObjects.add(new Fond(new Investor(toId)));
                }

            }

            return removedTradeObjects;
        }
    }

    @Override
    public int getTradeObjectAmount(String toId, String transactionId) throws RemoteException {
        synchronized (lock) {
            if (tradeObjects.get(toId) != null) {
                return tradeObjects.get(toId);
            }
            return 0;
        }
    }

    @Override
    public List<TradeObject> readAllTradeObjects(String transactionId) throws RemoteException {
        synchronized (lock) {
            ArrayList<TradeObject> allTradeObjects = new ArrayList<>();
            for (String key : tradeObjects.keySet()) {
                ArrayList<TradeObject> tempList = new ArrayList<>();
                for (int i = 0; i < tradeObjects.get(key); i++) {
                    if (knownCompanies.contains(key)) {
                        tempList.add(new Stock(new Company(key)));
                    } else {
                        tempList.add(new Fond(new Investor(key)));
                    }

                }
                allTradeObjects.addAll(tempList);
            }
            return allTradeObjects;
        }
    }

    @Override
    public String getDepotName() throws RemoteException {
        return "INVESTOR_DEPOT_" + investor.getId();
    }

    @Override
    public int getTotalAmountOfTradeObjects(String transactionId) throws RemoteException {
        synchronized (lock) {
            int totalAmount = 0;
            for (String key : tradeObjects.keySet()) {
                totalAmount += tradeObjects.get(key);
            }
            return totalAmount;
        }
    }

    @Override
    public void addTradeObjects(List<TradeObject> newTradeObjects, String transactionId) throws RemoteException {

        synchronized (lock) {
            if (newTradeObjects.size() > 0) {
                String stockId = newTradeObjects.get(0).getId();
                if (tradeObjects.containsKey(stockId)) {
                    tradeObjects.put(stockId, tradeObjects.get(stockId) + newTradeObjects.size());
                } else {
                    tradeObjects.put(stockId, newTradeObjects.size());
                }
            }
        }

        List<Serializable> sNewStocks = new ArrayList<>();
        for (TradeObject tradeObject: newTradeObjects) {
            sNewStocks.add((Serializable) tradeObject);
        }

        for (IRmiCallback<Serializable> callback: callbacks) {
            callback.newData(sNewStocks);
        }
    }

    @Override
    public void subscribe(IRmiCallback<Serializable> callback) throws RemoteException {
        callbacks.add(callback);
    }

    @Override
    public void unsubscribe(IRmiCallback<Serializable> callback) throws RemoteException {
        callbacks.remove(callback);
    }

    public String toString() {
        return "Depot of " + investor.getId();
    }

}
