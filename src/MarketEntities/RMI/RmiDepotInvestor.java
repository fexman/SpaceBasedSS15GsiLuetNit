package MarketEntities.RMI;

import MarketEntities.DepotInvestor;
import MarketEntities.Subscribing.ASubManager;
import Model.Company;
import Model.Investor;
import Model.Stock;
import Service.ConnectionError;
import Util.Container;

import java.util.List;

/**
 * Created by j0h1 on 01.05.2015.
 */
public class RmiDepotInvestor extends DepotInvestor {
    public RmiDepotInvestor(Investor investor, String transactionId) throws ConnectionError {
        super(investor, transactionId);

        this.depotName = Container.DEPOT_INVESTOR_TOKEN + investor.getId();
    }

    @Override
    public double getBudget(String transactionId) throws ConnectionError {
        return 0;
    }

    @Override
    public void setBudget(double amount, String transactionId) throws ConnectionError {

    }

    @Override
    public List<Stock> takeStocks(Company comp, int amount, String transactionId) throws ConnectionError {
        return null;
    }

    @Override
    public int getStockAmount(String stockName, String transactionId) throws ConnectionError {
        return 0;
    }

    @Override
    public List<Stock> readAllStocks(String transactionId) throws ConnectionError {
        return null;
    }

    @Override
    public int getTotalAmountOfStocks(String transactionId) throws ConnectionError {
        return 0;
    }

    @Override
    public void addStocks(List<Stock> stocks, String transactionId) throws ConnectionError {

    }

    @Override
    public void subscribe(ASubManager subscriber, String transactionId) throws ConnectionError {

    }
}
