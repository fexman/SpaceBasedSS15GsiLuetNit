package MarketEntities;

import Model.Company;
import Model.MarketValue;
import Service.ConnectionError;
import Service.Subscribing.MarketValues.AStockPricesSubManager;

import java.util.List;

/**
 * Created by Felix on 20.04.2015.
 */
public abstract class StockPricesContainer {

    public abstract void addOrUpdateMarketValue(MarketValue marketValue, String transactionId) throws ConnectionError;

    public abstract MarketValue getMarketValue(Company comp, String transactionId) throws ConnectionError;

    public abstract List<MarketValue> getStockPrices(String transactionId) throws ConnectionError;

    public abstract void subscribe(AStockPricesSubManager subscriber, String transactionId) throws ConnectionError;
}
