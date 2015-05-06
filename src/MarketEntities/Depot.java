package MarketEntities;

import Model.Stock;
import Service.ConnectionErrorException;

import java.util.List;

/**
 * Created by Felix on 14.04.2015.
 */
public abstract class Depot {

    //IMPORTANT, THIS HAS TO BE SET IN THE CONCRETE SUBCLASS
    protected String depotName;

    public String getDepotName() {
        return depotName;
    }

    public abstract int getTotalAmountOfStocks(String transactionId) throws ConnectionErrorException;

    public abstract void addStocks(List<Stock> stocks, String transactionId) throws ConnectionErrorException;

}
