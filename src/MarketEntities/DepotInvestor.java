package MarketEntities;

import Model.Company;
import Model.Investor;
import Model.Stock;
import Service.ConnectionError;

import java.util.List;

/**
 * Created by Felix on 14.04.2015.
 */
public abstract class DepotInvestor extends Depot {

    private String investorId;

    public DepotInvestor(String investorId, String transactionId) throws ConnectionError {
        this.investorId = investorId;
    }

    public String getInvestorId() {
        return investorId;
    }

    public abstract double getBudget(String transactionId) throws ConnectionError;

    public abstract void setBudget(double amount, String transactionId) throws ConnectionError;

    public void addToBudget(double amount,String transactionId) throws ConnectionError {
        setBudget(getBudget(transactionId) + amount, transactionId);
    }

    public abstract List<Stock> takeStocks(Company comp, int amount, String transactionId) throws ConnectionError;

    public abstract int getStockAmount(String stockName, String transactionId) throws ConnectionError;


}
