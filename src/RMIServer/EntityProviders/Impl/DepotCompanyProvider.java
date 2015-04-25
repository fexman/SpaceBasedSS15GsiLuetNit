package RMIServer.EntityProviders.Impl;

import Model.Company;
import Model.Stock;
import RMIServer.EntityProviders.IDepotCompanyProvider;

import java.rmi.RemoteException;
import java.util.ArrayList;
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
            for (int i = 1; i <= amount; i++) {
                tempStocks.add(new Stock(company));
            }
            stocks.remove(amount);
            return tempStocks;
        }
    }

    @Override
    public String getDepotName() throws RemoteException {
        return "COMPANY_DEPOT_"+company.getId();
    }

    @Override
    public int getTotalAmountOfStocks(String transactionId) throws RemoteException {
        synchronized (stocks) {
            return stocks.size();
        }
    }

    @Override
    public void addStocks(List<Stock> stocks, String transactionId) throws RemoteException {
        synchronized (stocks) {
            System.out.println("Got stocks: "+stocks.size());
            stocks.addAll(stocks);
        }
    }
}
