package MarketEntities;

import MarketEntities.XVSM.ISubscribeable;
import Model.Company;
import Model.Investor;
import Model.Stock;
import Service.ConnectionErrorException;

import java.util.List;

/**
 * Created by Felix on 14.04.2015.
 */
public abstract class DepotInvestor extends Depot implements ISubscribeable {

    private Investor investor;

    public DepotInvestor(Investor investor, String transactionId) throws ConnectionErrorException {
        this.investor = investor;
    }

    public Investor getInvestor() {
        return investor;
    }

    public abstract double getBudget(String transactionId) throws ConnectionErrorException;

    public abstract void setBudget(double amount, String transactionId) throws ConnectionErrorException;

    public void addToBudget(double amount,String transactionId) throws ConnectionErrorException {
        setBudget(getBudget(transactionId) + amount, transactionId);
    }

    public abstract List<Stock> takeStocks(Company comp, int amount, String transactionId) throws ConnectionErrorException;

    public abstract int getStockAmount(String stockName, String transactionId) throws ConnectionErrorException;

    public abstract List<Stock> readAllStocks(String transactionId) throws ConnectionErrorException;

}
