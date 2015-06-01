package RMIServer.EntityProviders.Impl;

import Model.Company;
import Model.Stock;
import Model.TradeObject;
import RMIServer.EntityProviders.IDepotCompanyProvider;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Felix on 22.04.2015.
 */
public class DepotCompanyProvider implements IDepotCompanyProvider {

    private Company company;
    private List<Stock> stocks;
    private Object lock;

    public DepotCompanyProvider(Company company) {
        this.company = company;
        this.stocks = new ArrayList<>();
        this.lock = new Object();
    }

    @Override
    public Company getCompany() throws RemoteException {
        return company;
    }

    @Override
    public List<Stock> takeStocks(int amount, String transactionId) throws RemoteException {
        synchronized (lock) {
            List<Stock> tempStocks = new ArrayList<>();

            for (int i = 0; i < (stocks.size()-amount);i++) {
                tempStocks.add(new Stock(company));
            }

            stocks = tempStocks;
            tempStocks = new ArrayList<>();

            for (int i = 0; i < amount; i++) {
                tempStocks.add(new Stock(company));
            }

            return tempStocks;
        }
    }

    @Override
    public String getDepotName() throws RemoteException {
        return "COMPANY_DEPOT_"+company.getId();
    }

    @Override
    public int getTotalAmountOfTradeObjects(String transactionId) throws RemoteException {
        synchronized (lock) {
            return stocks.size();
        }
    }

    @Override
    public void addTradeObjects(List<TradeObject> tradeObjects, String transactionId) throws RemoteException {
        synchronized (lock) {
            for (TradeObject tradeObject : tradeObjects) {
                if (tradeObject instanceof Stock) {
                    stocks.add((Stock)tradeObject);
                }
            }
        }
    }

    public String toString() {
        return "Depot of "+company.getId()+": "+stocks.size()+" stocks";
    }
}
