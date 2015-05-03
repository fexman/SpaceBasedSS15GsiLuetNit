package RMIServer.EntityProviders.Impl;

import MarketEntities.Subscribing.IRmiCallback;
import Model.Company;
import Model.Investor;
import Model.Stock;
import RMIServer.EntityProviders.IDepotInvestorProvider;

import java.rmi.RemoteException;
import java.util.*;

/**
 * Created by j0h1 on 02.05.2015.
 */
public class DepotInvestorProvider implements IDepotInvestorProvider {

    private Investor investor;
    private Double budget;
    private HashMap<String, ArrayList<Stock>> stocks;
    private Set<IRmiCallback<Stock>> callbacks;
    private Object lock;

    public DepotInvestorProvider(Investor investor) {
        this.investor = investor;
        this.stocks = new HashMap<>();
        this.budget = 0.0;
        this.lock = new Object();
    }

    @Override
    public Investor getInvestor() throws RemoteException {
        return investor;
    }

    @Override
    public double getBudget(String transactionId) throws RemoteException {
        return budget;
    }

    @Override
    public void setBudget(double amount, String transactionId) throws RemoteException {
        this.budget = amount;
    }

    @Override
    public List<Stock> takeStocks(Company comp, int amount, String transactionId) throws RemoteException {
        synchronized (lock) {
            ArrayList<Stock> stocksOfCompany = stocks.get(comp.getId());    // get stocks of company

            // delete amount of stocks to be taken out and create list of stocks to be returned
            List<Stock> takenStocks = new ArrayList<>();
            int deleted = 0;
            for (Iterator<Stock> iterator = stocksOfCompany.iterator(); iterator.hasNext(); ) {
                if (deleted == amount) {
                    break;
                }
                iterator.remove();
                takenStocks.add(new Stock(comp));

                deleted++;
            }

            return takenStocks;
        }
    }

    @Override
    public int getStockAmount(String stockName, String transactionId) throws RemoteException {
        if (stocks.get(stockName) != null) {
            return stocks.get(stockName).size();
        }
        return 0;
    }

    @Override
    public List<Stock> readAllStocks(String transactionId) throws RemoteException {
        ArrayList<Stock> allStocks = new ArrayList<>();
        for (String key : stocks.keySet()) {
            allStocks.addAll(stocks.get(key));
        }
        return allStocks;
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
                stocks.get(stockId).addAll(newStocks);
            }
        }
    }

    @Override
    public void subscribe(IRmiCallback<Stock> callback) throws RemoteException {
        callbacks.add(callback);
    }

    @Override
    public void unsubscribe(IRmiCallback<Stock> callback) throws RemoteException {
        callbacks.remove(callback);
    }

    public String toString() {
        return "Depot of " + investor.getId();
    }

}
