package MarketEntities;

import Model.Stock;
import Service.ConnectionError;

import java.util.List;

/**
 * Created by Felix on 14.04.2015.
 */
public abstract class Depot {

    protected String depotName;

    public String getDepotName() {
        return depotName;
    }

    public abstract int getTotalAmountOfStocks(String transactionId) throws ConnectionError;

    public abstract void addStocks(List<Stock> stocks, String transactionId) throws ConnectionError;

}
