package RMIServer.EntityProviders.Impl;

import MarketEntities.Subscribing.IRmiCallback;
import Model.Company;
import Model.Investor;
import Model.Stock;
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
    private HashMap<String, List<Stock>> stocks;
    private Set<IRmiCallback<Serializable>> callbacks;
    private Object lock;

    public DepotInvestorProvider(Investor investor) {
        this.investor = investor;
        this.stocks = new HashMap<>();
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
    public List<Stock> takeStocks(Company comp, int amount, String transactionId) throws RemoteException {
        synchronized (lock) {
            List<Stock> stocksOfCompany = stocks.get(comp.getId());    // get stocks of company
            List<Stock> tempStocks = new ArrayList<>();

            for (int i = 0; i < (stocksOfCompany.size()-amount);i++) {
                tempStocks.add(new Stock(comp));
            }

            stocksOfCompany = tempStocks;
            tempStocks = new ArrayList<>();

            for (int i = 0; i < amount; i++) {
                tempStocks.add(new Stock(comp));
            }

            return tempStocks;
        }
    }

    @Override
    public int getStockAmount(String stockName, String transactionId) throws RemoteException {
        synchronized (lock) {
            if (stocks.get(stockName) != null) {
                return stocks.get(stockName).size();
            }
            return 0;
        }
    }

    @Override
    public List<Stock> readAllStocks(String transactionId) throws RemoteException {
        synchronized (lock) {
            ArrayList<Stock> allStocks = new ArrayList<>();
            for (String key : stocks.keySet()) {
                allStocks.addAll(stocks.get(key));
            }
            return allStocks;
        }
    }

    @Override
    public String getDepotName() throws RemoteException {
        return "INVESTOR_DEPOT_" + investor.getId();
    }

    @Override
    public int getTotalAmountOfStocks(String transactionId) throws RemoteException {
        synchronized (lock) {
            int totalAmount = 0;
            for (String key : stocks.keySet()) {
                totalAmount += stocks.get(key).size();
            }
            return totalAmount;
        }
    }

    @Override
    public void addStocks(List<Stock> newStocks, String transactionId) throws RemoteException {

        synchronized (lock) {
            if (newStocks.size() > 0) {
                String stockId = newStocks.get(0).getCompany().getId();
                if (stocks.containsKey(stockId)) {
                    stocks.get(stockId).addAll(newStocks);
                } else {
                    stocks.put(stockId,newStocks);
                }
            }
        }

        List<Serializable> sNewStocks = new ArrayList<>();
        for (Stock s: newStocks) {
            sNewStocks.add((Serializable)s);
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
